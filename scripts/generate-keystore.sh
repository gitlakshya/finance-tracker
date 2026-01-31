#!/bin/bash

# Keystore Generation Script for Expense Tracker
# This script generates a release keystore for signing the Android app

set -e

echo "================================="
echo "Expense Tracker - Keystore Setup"
echo "================================="
echo ""

# Configuration
KEYSTORE_FILE="release-keystore.jks"
KEY_ALIAS="expense-tracker-release"
VALIDITY_DAYS=10000  # ~27 years

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo -e "${YELLOW}Warning: Keystore file '$KEYSTORE_FILE' already exists!${NC}"
    read -p "Do you want to overwrite it? (yes/NO): " -r
    echo
    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        echo "Aborting. Existing keystore preserved."
        exit 0
    fi
    rm "$KEYSTORE_FILE"
fi

echo "This script will generate a release keystore for signing your Android app."
echo "Please provide the following information:"
echo ""

# Collect information
read -p "Enter your full name: " FULL_NAME
read -p "Enter your organizational unit (e.g., Development): " ORG_UNIT
read -p "Enter your organization name (e.g., Company Name): " ORG_NAME
read -p "Enter your city or locality: " CITY
read -p "Enter your state or province: " STATE
read -p "Enter your two-letter country code (e.g., US, IN): " COUNTRY

echo ""
read -sp "Enter keystore password (min 6 characters): " STORE_PASSWORD
echo ""
read -sp "Confirm keystore password: " STORE_PASSWORD_CONFIRM
echo ""

if [ "$STORE_PASSWORD" != "$STORE_PASSWORD_CONFIRM" ]; then
    echo -e "${RED}Error: Passwords do not match!${NC}"
    exit 1
fi

if [ ${#STORE_PASSWORD} -lt 6 ]; then
    echo -e "${RED}Error: Password must be at least 6 characters!${NC}"
    exit 1
fi

echo ""
read -sp "Enter key password (can be same as keystore password): " KEY_PASSWORD
echo ""
read -sp "Confirm key password: " KEY_PASSWORD_CONFIRM
echo ""

if [ "$KEY_PASSWORD" != "$KEY_PASSWORD_CONFIRM" ]; then
    echo -e "${RED}Error: Key passwords do not match!${NC}"
    exit 1
fi

echo ""
echo "Generating keystore..."
echo ""

# Generate keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity $VALIDITY_DAYS \
    -storepass "$STORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$FULL_NAME, OU=$ORG_UNIT, O=$ORG_NAME, L=$CITY, ST=$STATE, C=$COUNTRY"

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Keystore generated successfully!${NC}"
    echo ""
    echo "Keystore details:"
    echo "  File: $KEYSTORE_FILE"
    echo "  Alias: $KEY_ALIAS"
    echo "  Algorithm: RSA 2048-bit"
    echo "  Validity: $VALIDITY_DAYS days"
    echo ""
    
    # Verify keystore
    echo "Verifying keystore..."
    keytool -list -v -keystore "$KEYSTORE_FILE" -storepass "$STORE_PASSWORD" | head -n 20
    
    echo ""
    echo -e "${YELLOW}IMPORTANT SECURITY NOTES:${NC}"
    echo "1. Store your keystore file and passwords securely!"
    echo "2. Keep multiple backups in different secure locations"
    echo "3. NEVER commit this keystore to version control"
    echo "4. NEVER share your keystore or passwords"
    echo "5. If you lose this keystore, you cannot update your app on Play Store"
    echo ""
    
    # Create gradle.properties template
    GRADLE_PROPS="gradle.properties.template"
    echo "Creating $GRADLE_PROPS with signing configuration..."
    cat > "$GRADLE_PROPS" << EOF
# Android Gradle Properties
# Copy this file to gradle.properties and fill in your actual values
# DO NOT commit gradle.properties to version control

# Keystore Configuration
RELEASE_STORE_FILE=$KEYSTORE_FILE
RELEASE_STORE_PASSWORD=<YOUR_STORE_PASSWORD>
RELEASE_KEY_ALIAS=$KEY_ALIAS
RELEASE_KEY_PASSWORD=<YOUR_KEY_PASSWORD>

# Build Configuration
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
kotlin.incremental=true
org.gradle.jvmargs=-Xmx2048m
EOF
    
    echo -e "${GREEN}✓ Created $GRADLE_PROPS${NC}"
    echo ""
    
    # Create secure backup reminder
    cat > "KEYSTORE_BACKUP_INSTRUCTIONS.txt" << EOF
KEYSTORE BACKUP INSTRUCTIONS
============================

Your keystore file: $KEYSTORE_FILE
Your key alias: $KEY_ALIAS

CRITICAL: This keystore is required to update your app on Google Play Store.
If you lose it, you will NEVER be able to update your published app.

BACKUP LOCATIONS (choose at least 3):
1. Secure cloud storage (encrypted, e.g., encrypted USB drive backed up to cloud)
2. Password-protected external hard drive
3. Company secure server/network drive
4. Password manager's secure notes
5. Bank safety deposit box (for production apps)

KEYSTORE DETAILS TO SAVE SECURELY:
- Keystore file location
- Keystore password
- Key alias
- Key password
- Generation date: $(date)
- Validity expires: $(date -d "+$VALIDITY_DAYS days" 2>/dev/null || date -v +${VALIDITY_DAYS}d 2>/dev/null || echo "Calculate manually")

PASSWORDS:
Store your passwords in a secure password manager like:
- 1Password
- LastPass
- Bitwarden
- Your company's password management system

TEST YOUR BACKUP:
After backing up, try restoring from backup to verify it works.

TEAM ACCESS:
If working in a team:
- Document who has access to the keystore
- Use a shared secure storage solution
- Have a succession plan
- Consider using a CI/CD service's secret management

EOF
    
    echo -e "${YELLOW}Created KEYSTORE_BACKUP_INSTRUCTIONS.txt${NC}"
    echo "Read this file for backup instructions!"
    echo ""
    
    # Generate secure configuration for CI/CD
    echo "Generating base64 encoded keystore for CI/CD..."
    BASE64_KEYSTORE=$(base64 -w 0 "$KEYSTORE_FILE" 2>/dev/null || base64 "$KEYSTORE_FILE")
    
    CI_SETUP_FILE="CI_CD_SETUP.txt"
    cat > "$CI_SETUP_FILE" << EOF
CI/CD SETUP INSTRUCTIONS
========================

For GitHub Actions, set these secrets in your repository:
Repository Settings > Secrets and variables > Actions > New repository secret

Required Secrets:
-----------------
Name: RELEASE_KEYSTORE_BASE64
Value: <paste the base64 string below>

Name: RELEASE_STORE_PASSWORD
Value: <your keystore password>

Name: RELEASE_KEY_ALIAS
Value: $KEY_ALIAS

Name: RELEASE_KEY_PASSWORD
Value: <your key password>

Base64 Encoded Keystore (for RELEASE_KEYSTORE_BASE64):
-------------------------------------------------------
$BASE64_KEYSTORE

IMPORTANT: Copy the above base64 string completely!

Additional CI/CD Secrets (optional):
------------------------------------
Name: PLAY_STORE_SERVICE_ACCOUNT
Value: <Google Play Console service account JSON>

Name: SLACK_WEBHOOK_URL
Value: <Slack webhook for notifications>

EOF
    
    echo -e "${GREEN}✓ Created $CI_SETUP_FILE${NC}"
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}Keystore setup completed successfully!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Read KEYSTORE_BACKUP_INSTRUCTIONS.txt and backup your keystore"
    echo "2. Copy gradle.properties.template to gradle.properties"
    echo "3. Fill in your passwords in gradle.properties"
    echo "4. For CI/CD: Read CI_CD_SETUP.txt and configure GitHub secrets"
    echo "5. Add $KEYSTORE_FILE to .gitignore (should already be there)"
    echo ""
    
else
    echo -e "${RED}Error: Failed to generate keystore!${NC}"
    exit 1
fi
