# PostgreSQL Restore

A custom postgres base image for restore backup with uid 1000.

This is atomic routine for any CI/CD such as gitlab-ci, bitbucket-pipelines etc.

In any case is possible to use manually with run.sh helper script.

## Configuration

List of required envs. This envs will be set on your pipeline variables or in your .env file for manual usage.

| Key      | Value           |
| ------------- |:-------------:|
| AWS_ACCESS_KEY_ID      | <access-key> |
| AWS_SECRET_ACCESS_KEY      | <secret-key>      |
| AWS_DEFAULT_REGION | eu-central-1      |
| BACKUP_BUCKET_NAME | <bucket-name>      |
| RESTORE_TAG | <datetime-format> or latest      |
| RESTORE_SCHEMA | <db-name> or void      |
| APP_NAME | <repo-slug>      |
| DB_HOST | <db-host>      |
| DB_PORT | <db-port>      |
| DB_USERNAME | <db-username>      |
| DB_PASSWORD | <db-password>      |

## Usage

Paste this command in your pipeline step:

`docker run --rm -t --env AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}" --env AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}" --env AWS_DEFAULT_REGION="${AWS_DEFAULT_REGION}" --env BACKUP_BUCKET_NAME="${BACKUP_BUCKET_NAME}" --env RESTORE_TAG="${RESTORE_TAG}" --env DB_HOST="${DB_HOST}" --env DB_USERNAME="${DB_USERNAME}" --env DB_PORT="${DB_PORT}" --env RESTORE_SCHEMA="${RESTORE_SCHEMA}" --env APP_NAME="${APP_NAME}" --env DB_PASSWORD="$(echo $(sed -e 's/\$/\\$/g' <<< $DB_PASSWORD))"  sindriainc/postgres-restore:1.0.0`

OR use the helper script:

`bash run.sh`

IMPORTANT: `Remeber to set all envs in your .env file before run.`