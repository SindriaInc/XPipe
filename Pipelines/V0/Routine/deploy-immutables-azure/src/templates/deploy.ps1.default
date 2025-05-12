#Prepare the VM parameters

$rgName = ""

$location = "australiaEast"

$vnet = ""

$subnet = "/subscriptions/xxxxxxxxx/resourceGroups//providers/Microsoft.Network/virtualNetworks//subnets/"

$nicName = "VM01-Nic-01"

$vmName = "VM01"

$osDiskName = "VM01-OSDisk"

$osDiskUri = "https://.blob.core.windows.net//server.vhd"

$VMSize = "Standard_A1"

$storageAccountType = "StandardLRS"

$IPaddress = "10.10.10.10"

#Create the VM resources

$IPconfig = New-AzureRmNetworkInterfaceIpConfig -Name "IPConfig1" -PrivateIpAddressVersion IPv4 -PrivateIpAddress $IPaddress -SubnetId $subnet

$nic = New-AzureRmNetworkInterface -Name $nicName -ResourceGroupName $rgName -Location $location -IpConfiguration $IPconfig

$vmConfig = New-AzureRmVMConfig -VMName $vmName -VMSize $VMSize

$vm = Add-AzureRmVMNetworkInterface -VM $vmConfig -Id $nic.Id

$osDisk = New-AzureRmDisk -DiskName $osDiskName -Disk (New-AzureRmDiskConfig -AccountType $storageAccountType -Location $location -CreateOption Import -SourceUri $osDiskUri) -ResourceGroupName $rgName

$vm = Set-AzureRmVMOSDisk -VM $vm -ManagedDiskId $osDisk.Id -StorageAccountType $storageAccountType -DiskSizeInGB 128 -CreateOption Attach -Windows

$vm = Set-AzureRmVMBootDiagnostics -VM $vm -disable

#Create the new VM

New-AzureRmVM -ResourceGroupName $rgName -Location $location -VM $vm