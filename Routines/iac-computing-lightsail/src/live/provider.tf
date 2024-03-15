# Configure the AWS Provider
provider "aws" {
  region = "${local.common.providers.aws.region}"
}