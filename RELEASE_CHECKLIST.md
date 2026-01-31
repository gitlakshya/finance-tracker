# Expense Tracker - Release Checklist

## Pre-Release Checklist

### Code Quality
- [ ] All unit tests passing (target: 100%)
- [ ] All instrumented tests passing
- [ ] Lint checks passing with no errors
- [ ] Code review completed
- [ ] No hardcoded credentials or API keys
- [ ] All TODO/FIXME comments addressed
- [ ] Memory leaks checked and fixed
- [ ] Performance profiling completed

### Version & Build
- [ ] `versionCode` incremented in `build.gradle.kts`
- [ ] `versionName` updated following semantic versioning
- [ ] Build configuration validated:
  - [ ] Release variant builds successfully
  - [ ] ProGuard rules tested
  - [ ] APK size optimized (<50MB recommended)
  - [ ] AAB (Android App Bundle) generated
- [ ] Keystore and signing configured correctly

### Permissions & Security
- [ ] Permissions list reviewed and justified
- [ ] Runtime permission requests tested
- [ ] Sensitive data encrypted
- [ ] Network security config reviewed
- [ ] ProGuard/R8 obfuscation enabled
- [ ] SSL pinning implemented (if applicable)
- [ ] Data backup rules configured

### Database & Data
- [ ] Database migrations tested
- [ ] Room schema export configured
- [ ] Data persistence tested
- [ ] Export/Import functionality tested
- [ ] Database encryption reviewed (if needed)

### Features & UI
- [ ] All screens tested on different screen sizes
- [ ] Dark mode support verified
- [ ] Landscape orientation tested
- [ ] Accessibility features tested:
  - [ ] TalkBack/screen readers
  - [ ] Content descriptions
  - [ ] Minimum touch target sizes (48dp)
- [ ] Localization complete (if multi-language)
- [ ] All images optimized (WebP format)
- [ ] Empty states handled gracefully
- [ ] Error states with user-friendly messages
- [ ] Loading states implemented

### SMS & Permissions
- [ ] SMS reading permissions handled correctly
- [ ] SMS parser tested with real bank SMS
- [ ] Notification permission requested (Android 13+)
- [ ] Background restrictions handled

### Testing
- [ ] Manual testing on multiple devices:
  - [ ] Low-end device (Android 10)
  - [ ] Mid-range device (Android 12)
  - [ ] High-end device (Android 14)
- [ ] Different Android versions tested:
  - [ ] Android 10 (API 29)
  - [ ] Android 11 (API 30)
  - [ ] Android 12 (API 31)
  - [ ] Android 13 (API 33)
  - [ ] Android 14 (API 34)
- [ ] Monkey testing completed
- [ ] Battery usage optimized
- [ ] Network conditions tested (offline mode)

### Documentation
- [ ] README.md updated
- [ ] CHANGELOG.md updated with release notes
- [ ] API documentation current
- [ ] User guide/help section reviewed
- [ ] Privacy policy updated
- [ ] Terms of service reviewed

### Play Store Assets
- [ ] App icon (512x512 PNG)
- [ ] Feature graphic (1024x500)
- [ ] Screenshots (minimum 2, recommend 8):
  - [ ] Phone screenshots (portrait)
  - [ ] Tablet screenshots (optional)
- [ ] Short description (<80 characters)
- [ ] Full description (<4000 characters)
- [ ] App category selected
- [ ] Content rating completed
- [ ] Target age group set
- [ ] What's New / Release notes (<500 characters)

### Legal & Compliance
- [ ] Privacy policy URL provided
- [ ] Terms of service URL (if applicable)
- [ ] GDPR compliance (if targeting EU)
- [ ] COPPA compliance (if for children)
- [ ] Data deletion process documented
- [ ] Third-party licenses included

### Release Configuration
- [ ] Signing configuration secured
- [ ] Keystore backed up securely
- [ ] Release notes prepared
- [ ] Version tags created in git
- [ ] Release branch created (if using GitFlow)

### Play Store Listing
- [ ] App title optimized (max 30 characters)
- [ ] Keywords researched and included
- [ ] Competitor analysis completed
- [ ] Pricing & distribution set
- [ ] Countries/regions selected
- [ ] Device categories configured
- [ ] Pre-registration (if planned)

### Post-Release Preparation
- [ ] Crash reporting configured (Firebase/Crashlytics)
- [ ] Analytics configured (Firebase Analytics)
- [ ] Performance monitoring set up
- [ ] Remote config initialized (optional)
- [ ] Support email configured
- [ ] Response templates prepared
- [ ] Rollout strategy decided:
  - [ ] Internal testing
  - [ ] Closed testing (alpha)
  - [ ] Open testing (beta)
  - [ ] Staged rollout plan (5% → 100%)

### Monitoring Setup
- [ ] Play Console notifications enabled
- [ ] Alert thresholds configured:
  - [ ] Crash rate alert (>1%)
  - [ ] ANR rate alert (>0.5%)
  - [ ] Low rating alert (<3.5)
- [ ] Team communication channels set up
- [ ] On-call rotation defined (if applicable)

### Rollback Plan
- [ ] Previous version APK/AAB archived
- [ ] Rollback procedure documented
- [ ] Hotfix branch strategy defined
- [ ] Emergency contact list prepared

## Release Day Checklist

### Morning (Before Release)
- [ ] All above pre-release items completed
- [ ] Team notified of release schedule
- [ ] Monitoring dashboards open
- [ ] Support team briefed

### Release Execution
- [ ] Upload AAB to Play Console
- [ ] Fill release notes
- [ ] Set rollout percentage (start with 5-10%)
- [ ] Submit for review
- [ ] Tag release in version control
- [ ] Update project management tools

### Post-Release (First 24 Hours)
- [ ] Monitor crash reports hourly
- [ ] Check Play Store reviews
- [ ] Monitor ANR rates
- [ ] Track installation stats
- [ ] Check server logs (if applicable)
- [ ] Verify analytics data flowing
- [ ] Test production version on device

### Post-Release (First Week)
- [ ] Daily crash report review
- [ ] Increase rollout percentage incrementally
- [ ] Respond to user reviews
- [ ] Track key metrics:
  - [ ] Install rate
  - [ ] Uninstall rate
  - [ ] Retention (Day 1, Day 3, Day 7)
  - [ ] Crash-free users %
  - [ ] Average rating
- [ ] Plan hotfix if critical issues found

## Release Types

### Internal Testing
- Purpose: Team testing
- Rollout: Immediate to testers
- Duration: 1-2 days

### Closed Testing (Alpha)
- Purpose: Limited external testing
- Rollout: Invite-only
- Duration: 3-7 days
- Tester limit: Unlimited

### Open Testing (Beta)
- Purpose: Public beta program
- Rollout: Anyone can join
- Duration: 7-14 days
- Feedback collection important

### Production Release
- Purpose: All users
- Rollout: Staged (5% → 10% → 25% → 50% → 100%)
- Duration: 7-14 days for full rollout

## Hotfix Checklist

If critical bug found after release:

- [ ] Halt current rollout immediately
- [ ] Assess severity and impact
- [ ] Create hotfix branch from release tag
- [ ] Fix bug with minimal changes
- [ ] Test fix thoroughly
- [ ] Increment versionCode and patch version
- [ ] Fast-track through internal testing only
- [ ] Release with updated "What's New"
- [ ] Resume or restart rollout

## Metrics to Monitor

### Health Metrics
- Crash-free users: Target >99.5%
- ANR rate: Target <0.5%
- App start time: Target <2 seconds
- Memory usage: Stay under 200MB

### User Metrics
- Daily Active Users (DAU)
- Monthly Active Users (MAU)
- DAU/MAU ratio: Target >20%
- Retention Day 1: Target >40%
- Retention Day 7: Target >20%
- Retention Day 30: Target >10%

### Play Store Metrics
- Average rating: Target >4.0
- Install rate
- Uninstall rate: Target <5%
- Rating velocity
- Review sentiment

## Emergency Contacts

- Technical Lead: [Name] - [Email/Phone]
- Product Manager: [Name] - [Email/Phone]
- QA Lead: [Name] - [Email/Phone]
- DevOps: [Name] - [Email/Phone]
- Support Team: [Email]

## Notes

- Always test in staging environment first
- Keep keystore password in secure location
- Document all changes in CHANGELOG.md
- Celebrate successful releases! 🎉
