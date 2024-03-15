# Setup Instance


## Update System


### Ubuntu Server

- Update repository: `sudo apt update`
- Update packeges: `sudo apt upgrade`

### Fedora Server

- Update repository and packages: `sudo dnf update`

### CentOS 

- Update repository and packages: `sudo yum update`


## Setup users and groups


- Show user: $ `cat /etc/passwd`
- Show groups: $ `cat /etc/groups`
- Show sudoers main config and match default sudoers group: `visudo`

```
#
# This file MUST be edited with the 'visudo' command as root.
#
# Please consider adding local content in /etc/sudoers.d/ instead of
# directly modifying this file.
#
# See the man page for details on how to write a sudoers file.
#
Defaults        env_reset
Defaults        mail_badpass
Defaults        secure_path="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/snap/bin"

# Host alias specification

# User alias specification

# Cmnd alias specification

# User privilege specification
root    ALL=(ALL:ALL) ALL

# Members of the admin group may gain root privileges
%admin ALL=(ALL) ALL

# Allow members of group sudo to execute any command
%sudo   ALL=(ALL:ALL) NOPASSWD: ALL

# See sudoers(5) for more information on "#include" directives:

#includedir /etc/sudoers.d
```


- Show user groups and UID: $ `id <username>`
 
N.B. When you add a linux user, it create automatically a group with the same ID. So the UID will be the same to the GID if it will be no specified in the user creation step. Normally it will stat with the UID 1000.


- Create nominal user: `useradd -m -s /bin/bash <username>`
- Setup sudoer permission with group: `usermod -a -G <groupname> <username>`
- Lock the password: `passwd -l <username>`
- Dispay hostname: `hostname` 
- Hostname configuration: `vim /etc/hostname`
- Setup hostname without reboot system: `hostnamectl set-hostname $(cat /etc/hostname)`
- Allows people in group sudo to run all commands without password: `visudo`
  %sudo ALL=(ALL) NOPASSWD: ALL


## Setup SSH keys

- Move into Home User Directory `cd /home/<username>`
- Create directory: `mkdir -p .ssh`
- Change the permission for the directory ssh: `chmod 700 .ssh`
- Create an empty file that will contain the public key named authorized_keys: `touch .ssh/authorized_keys`
- Change permission for the file: `chmod 600 .ssh/authorized_keys`
- Copy the ssh public key and paste it on the file: `vim authorized_key`
- Change the owner for all home of the user recursivelly: `chown -R <username>:<group> ./`



## Setup firewall 


ubuntu tool: `ufw status`

or for expert: `iptables -S`


TODO: setup firewall (next step)


## Setup SSH daemon


- Change default LISTENING port from 22 to custom with 4 numbers: `systemctl status sshd`
- Disable root login: `vim /etc/ssh/sshd_config`
  
```
  Port 5872
  PermitRouteLogin no
  MaxAuthTries 3
  PasswordAuthentication no
  ClientAliveInterval 60
  ClientAliveCountMax 60
```

N.B. Check the all file before write and quit

- Restart ssh daemon: `systemctl restart sshd`
- Check daemon ssh again `systemctl status sshd` or/and `netstat -polenta`


## Tips and Tricks

 - Edit default user with UID 1000 if already exists: `usermod -m -d /home/sindria -l sindria ubuntu -c "Sindria User" sindria`