# Prepare the VM parameters

$rgName = "@@IMMUTABLE_RESOURCE_GROUP@@"

$location = "@@IMMUTABLE_REGION@@"

$vnet = "@@IMMUTABLE_VPC@@"

$subnet = "/subscriptions/@@IMMUTABLE_SUBNET@@/resourceGroups//providers/Microsoft.Network/virtualNetworks//subnets/"

$nicName = "@@IMMUTABLE_NAME@@-Nic-01"

$vmName = "@@IMMUTABLE_NAME@@"

$osDiskName = "@@IMMUTABLE_NAME@@-OSDisk"

$osDiskUri = "@@IMMUTABLE_BLUEPRINT@@"

$VMSize = "@@IMMUTABLE_BUNDLE@@"

$storageAccountType = "@@IMMUTABLE_STORAGE_ACCOUNT_TYPE@@"

$IPaddress = "@@IMMUTABLE_PRIVATE_IP_ADDRESS@@"

# Create the VM resources

$IPconfig = New-AzureRmNetworkInterfaceIpConfig -Name "IPConfig1" -PrivateIpAddressVersion IPv4 -PrivateIpAddress $IPaddress -SubnetId $subnet

$nic = New-AzureRmNetworkInterface -Name $nicName -ResourceGroupName $rgName -Location $location -IpConfiguration $IPconfig

$vmConfig = New-AzureRmVMConfig -VMName $vmName -VMSize $VMSize

$vm = Add-AzureRmVMNetworkInterface -VM $vmConfig -Id $nic.Id

$osDisk = New-AzureRmDisk -DiskName $osDiskName -Disk (New-AzureRmDiskConfig -AccountType $storageAccountType -Location $location -CreateOption Import -SourceUri $osDiskUri) -ResourceGroupName $rgName

$vm = Set-AzureRmVMOSDisk -VM $vm -ManagedDiskId $osDisk.Id -StorageAccountType $storageAccountType -DiskSizeInGB 128 -CreateOption Attach -Windows

$vm = Set-AzureRmVMBootDiagnostics -VM $vm -disable

# Create the new VM

New-AzureRmVM -ResourceGroupName $rgName -Location $location -VM $vm