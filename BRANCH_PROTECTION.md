# Branch Protection Rules

This document outlines the recommended branch protection rules for the Finance Tracker repository to ensure code quality and prevent broken code from being merged.

## Table of Contents
- [Main Branch Protection](#main-branch-protection)
- [Develop Branch Protection](#develop-branch-protection)
- [Setting Up Protection Rules](#setting-up-protection-rules)
- [Status Checks](#status-checks)
- [Bypassing Protection Rules](#bypassing-protection-rules)

---

## Main Branch Protection

The `main` branch represents production-ready code and should have the strictest protection rules.

### Required Settings

#### 1. Require Pull Request Reviews
- **Required approving reviews**: 1 (minimum)
- **Dismiss stale pull request approvals when new commits are pushed**: ✅ Enabled
- **Require review from Code Owners**: ✅ Enabled (if CODEOWNERS file exists)
- **Restrict who can dismiss pull request reviews**: ✅ Enabled (limit to maintainers)

#### 2. Require Status Checks to Pass
- **Require branches to be up to date before merging**: ✅ Enabled
- **Required status checks**:
  - `code-quality / Code Quality Checks`
  - `test / Unit Tests`
  - `build / Build Debug APK`
  - `quality-gate / Quality Gate`

#### 3. Require Conversation Resolution
- **Require conversation resolution before merging**: ✅ Enabled
- All review comments must be resolved before merge

#### 4. Require Signed Commits
- **Require signed commits**: ✅ Enabled (recommended)
- Ensures commit authenticity

#### 5. Require Linear History
- **Require linear history**: ✅ Enabled
- Forces rebase or squash merges, keeps history clean

#### 6. Include Administrators
- **Include administrators**: ✅ Enabled
- Even admins must follow the rules

#### 7. Restrict Push Access
- **Restrict who can push to matching branches**: ✅ Enabled
- No direct pushes allowed, all changes via PR

#### 8. Allow Force Pushes
- **Allow force pushes**: ❌ Disabled
- Prevents history rewriting

#### 9. Allow Deletions
- **Allow deletions**: ❌ Disabled
- Prevents accidental branch deletion

---

## Develop Branch Protection

The `develop` branch is for ongoing development and should have slightly relaxed rules compared to `main`.

### Required Settings

#### 1. Require Pull Request Reviews
- **Required approving reviews**: 1
- **Dismiss stale pull request approvals when new commits are pushed**: ✅ Enabled
- **Require review from Code Owners**: ⚠️ Optional

#### 2. Require Status Checks to Pass
- **Require branches to be up to date before merging**: ✅ Enabled
- **Required status checks**:
  - `code-quality / Code Quality Checks`
  - `test / Unit Tests`
  - `build / Build Debug APK`

#### 3. Require Conversation Resolution
- **Require conversation resolution before merging**: ✅ Enabled

#### 4. Require Signed Commits
- **Require signed commits**: ⚠️ Optional

#### 5. Require Linear History
- **Require linear history**: ⚠️ Optional

#### 6. Restrict Push Access
- **Restrict who can push to matching branches**: ✅ Enabled
- Core team members only

#### 7. Allow Force Pushes
- **Allow force pushes**: ❌ Disabled

#### 8. Allow Deletions
- **Allow deletions**: ❌ Disabled

---

## Setting Up Protection Rules

### GitHub UI Steps

1. **Navigate to Repository Settings**
   - Go to your repository on GitHub
   - Click on "Settings" tab
   - Select "Branches" from the left sidebar

2. **Add Branch Protection Rule**
   - Click "Add branch protection rule"
   - Enter branch name pattern (e.g., `main` or `develop`)

3. **Configure Protection Settings**
   - Enable required settings as outlined above
   - Select required status checks from the list
   - Save changes

### Using GitHub API (Automated Setup)

```bash
# Set up main branch protection
gh api \
  --method PUT \
  -H "Accept: application/vnd.github+json" \
  /repos/{owner}/{repo}/branches/main/protection \
  -f required_status_checks='{"strict":true,"contexts":["code-quality / Code Quality Checks","test / Unit Tests","build / Build Debug APK","quality-gate / Quality Gate"]}' \
  -f enforce_admins=true \
  -f required_pull_request_reviews='{"dismissal_restrictions":{},"dismiss_stale_reviews":true,"require_code_owner_reviews":true,"required_approving_review_count":1}' \
  -f restrictions=null \
  -f required_linear_history=true \
  -f allow_force_pushes=false \
  -f allow_deletions=false \
  -f required_conversation_resolution=true

# Set up develop branch protection
gh api \
  --method PUT \
  -H "Accept: application/vnd.github+json" \
  /repos/{owner}/{repo}/branches/develop/protection \
  -f required_status_checks='{"strict":true,"contexts":["code-quality / Code Quality Checks","test / Unit Tests","build / Build Debug APK"]}' \
  -f enforce_admins=false \
  -f required_pull_request_reviews='{"dismissal_restrictions":{},"dismiss_stale_reviews":true,"required_approving_review_count":1}' \
  -f restrictions=null \
  -f allow_force_pushes=false \
  -f allow_deletions=false \
  -f required_conversation_resolution=true
```

---

## Status Checks

### Required CI/CD Checks

All the following checks must pass before a PR can be merged:

#### 1. Code Quality Checks (`code-quality`)
- **Detekt**: Static code analysis for Kotlin
  - Checks for code smells
  - Enforces complexity limits
  - Validates naming conventions
  - Maximum issues: 0

- **Android Lint**: Android-specific code analysis
  - Checks for Android API usage issues
  - Identifies performance problems
  - Detects security vulnerabilities
  - Must pass with no errors

- **Kotlin Type Check**: Compile-time type checking
  - Ensures no compilation errors
  - Validates type safety
  - Must compile successfully

#### 2. Unit Tests (`test`)
- All unit tests must pass
- Minimum test coverage: 70% (recommended)
- No flaky tests allowed
- Test execution time: < 5 minutes

#### 3. Build (`build`)
- Debug APK must build successfully
- APK size should be monitored
- Build time: < 10 minutes

#### 4. Quality Gate (`quality-gate`)
- Aggregate check ensuring all previous checks passed
- Final gatekeeper before merge
- Cannot be bypassed

### Optional Checks (Informational)
- **Security Scan**: Dependency vulnerability check
  - Runs but doesn't block merge
  - Warnings should be reviewed
  - Critical issues should be addressed ASAP

---

## Bypassing Protection Rules

### When Bypassing is Acceptable
- **Hotfix for Critical Production Bug**: Requires admin approval
- **CI/CD Pipeline Failure**: Only if pipeline issue is unrelated to code changes
- **Documentation-Only Changes**: Can be considered for fast-track merge

### Bypass Process
1. **Request Admin Override**
   - Document reason for bypass
   - Get approval from at least 2 maintainers
   - Create incident report

2. **Post-Bypass Actions**
   - Create follow-up PR to fix any skipped checks
   - Update documentation if bypass revealed process gaps
   - Review and improve CI/CD if pipeline caused false failures

### Emergency Hotfix Workflow
```bash
# Create hotfix branch from main
git checkout main
git pull origin main
git checkout -b hotfix/critical-bug-fix

# Make changes and commit
git add .
git commit -m "hotfix: Critical bug fix description"

# Push and create PR with [HOTFIX] tag
git push origin hotfix/critical-bug-fix
# Create PR with title: [HOTFIX] Description
# Request admin bypass approval
# Merge with admin override
# Backport to develop
```

---

## Branch Workflow

### Feature Development
```
feature/new-feature → develop → main
```

### Hotfix Development
```
hotfix/critical-fix → main → develop
```

### Release Process
```
develop → release/v1.0.0 → main
                          ↓
                       develop (backmerge)
```

---

## Enforcement Timeline

### Phase 1: Warning Period (Week 1-2)
- Protection rules enabled but bypassable
- Team members receive warnings on violations
- Focus on education and CI/CD stability

### Phase 2: Soft Enforcement (Week 3-4)
- Protection rules enforced for develop branch
- Main branch still allows admin bypass
- Monitor for false positives

### Phase 3: Full Enforcement (Week 5+)
- All protection rules fully enforced
- No bypasses except emergency hotfixes
- Regular review of rule effectiveness

---

## Monitoring and Reporting

### Weekly Reports
- Number of PRs merged
- Average time to merge
- Number of failed status checks
- Number of bypass requests
- Common failure reasons

### Monthly Review
- Review protection rule effectiveness
- Adjust rules based on team feedback
- Update CI/CD pipeline as needed
- Identify process improvements

---

## Contact

For questions about branch protection rules or to request exceptions:
- Create an issue with the `branch-protection` label
- Tag repository maintainers
- Escalate to project lead if urgent

---

## References

- [GitHub Branch Protection Documentation](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [GitHub Required Status Checks](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches#require-status-checks-before-merging)
- [CI/CD Workflow Documentation](.github/workflows/README.md)
