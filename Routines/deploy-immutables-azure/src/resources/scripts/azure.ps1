#!/usr/bin/env pwsh

# Install PowerShellGet utils
Install-Module -Name PowerShellGet -AcceptLicense -Confirm:$false -Force -SkipPublisherCheck

# Install Azure PowerShell
Install-Module -Name Az -AcceptLicense -Confirm:$false -Repository PSGallery -Force -SkipPublisherCheck

# Install Azure RM PowerShell
Install-Module -Name AzureRM.Netcore -AcceptLicense -Confirm:$false -AllowClobber -Force -SkipPublisherCheck

Get-Module -ListAvailable

Install-Module -Name AzureRM.DataFactoryV2 -Verbose -AcceptLicense -Confirm:$false -Force -SkipPublisherCheck

Get-Module -ListAvailable

Install-Module -Name AzureRM -Verbose -AcceptLicense -Confirm:$false -Force -SkipPublisherCheck