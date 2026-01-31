# GitHub Actions Workflows

## Active Workflows

### 1. Android CI (`.github/workflows/android-ci.yml`)
**Trigger**: Pull Requests to `main` or `develop`
**Purpose**: Quality gates for code review
**Jobs**:
- code-quality: Lint + Type checking
- test: Unit tests (39 tests)
- build: Debug APK
- security: Dependency scan
- quality-gate: Aggregate check

**When it runs**: Only when creating/updating PRs
**Merge blocking**: Yes - all checks must pass

---

### 2. Android Staging (`.github/workflows/android-staging.yml`)
**Trigger**: Push to `develop` branch
**Purpose**: Build development APK for testing
**Jobs**:
- Build development debug APK
- Upload artifact

**When it runs**: Every push to `develop`
**Output**: `app-staging` artifact (development-debug.apk)

---

### 3. Android Release (`.github/workflows/android-release.yml`)
**Trigger**: Git tags (v*.*.*)
**Purpose**: Production release builds
**Jobs**:
- Build release APK and AAB
- Create GitHub release
- (Optional) Deploy to Play Store

**When it runs**: When you create a version tag (e.g., v1.0.0)
**Output**: Production-signed APK/AAB

---

## Workflow Summary

| Workflow | Trigger | Runs On | Purpose | Blocks Merge |
|----------|---------|---------|---------|--------------|
| **CI** | Pull Request | main, develop | Quality checks | ✅ Yes |
| **Staging** | Push | develop | Dev builds | ❌ No |
| **Release** | Tag | v*.*.* | Production | ❌ No |

---

## Why Only Two Workflows Run

**For Pull Requests**:
- Only `android-ci.yml` runs
- Checks code quality, tests, and build
- **Blocks merge if fails**

**For Pushes to develop**:
- Only `android-staging.yml` runs
- Builds development APK
- No merge blocking (post-merge)

**For Pushes to main**:
- Nothing runs automatically
- Developers should merge via PRs (which trigger CI)

**For Tags (v1.0.0)**:
- Only `android-release.yml` runs
- Builds production release

---

## No Duplicate Runs

✅ **Fixed**: Previously both `push` and `pull_request` triggers caused duplicate runs
✅ **Now**: Each event triggers only one appropriate workflow

---

## How to Use

### For Development (develop branch):
```bash
git checkout develop
git add .
git commit -m "feat: Add feature"
git push origin develop
# → Triggers: android-staging.yml
# → Output: Development APK in Actions artifacts
```

### For Production (main branch):
```bash
# Create PR from develop to main
gh pr create --base main --head develop
# → Triggers: android-ci.yml (runs all quality checks)
# → Merge PR after checks pass

# After merge, create release tag
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
# → Triggers: android-release.yml
# → Output: Production APK/AAB + GitHub Release
```

---

## Quality Gates Active

Every PR automatically checks:
- ✅ Android Lint (no errors)
- ✅ Kotlin type checking (compiles)
- ✅ Unit tests (all 39 pass)
- ✅ Build verification (APK builds)
- ✅ Dependency security

**Merge is blocked until all checks pass!**
