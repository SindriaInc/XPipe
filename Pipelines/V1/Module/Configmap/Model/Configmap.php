<?php

namespace Pipelines\Configmap\Model;

use Magento\Framework\DataObject;

class Configmap extends DataObject
{

    /**
     * Singleton instance
     *
     * @var object $instance
     */
    private static object $instance;

    /**
     * Get singleton instance
     *
     * @return \Pipelines\Configmap\Model\Configmap
     */
    public static function getInstance() : \Pipelines\Configmap\Model\Configmap
    {
        if (!isset(self::$instance)) {
            $className = __CLASS__;
            self::$instance = new $className();
        }

        return self::$instance;
    }

    // Required fields

    private string $configmapId;

   private string $owner;

   private string $configMapName;

   // AWS fields

   private string $awsAccessKeyId;

   private string $awsSecretAccessKey;

   private string $awsDefaultRegion;

    private string $eksClusterName;


    // Azure fields

    private string $azureSubscriptionId;

    private string $azureClientId;

    private string $azureSecret;

    private string $azureTenant;

    private string $azureResourceGroup;

    private string $azureStorageAccount;

    private string $azureStorageAccessKey;

    private string $azureStorageConnectionString;

   private string $azureIni;

   private string $azureConf;

   // Docker Hub fields

   private string $dockerhubUsername;
   private string $dockerhubPassword;

   private string $dockerhubNamespace;
   private string $dockerhubPrivateNamespace;

   // SCM Git fields

    private string $scmGitProtocol;

    private string $scmGitFqdn;

    private string $scmGitNamespace;

    private string $scmGitUsername;

    private string $scmGitPassword;

    private string $scmGitAccessToken;


   // CRT Certbot fields

   private string $crtCertbotCache;
   private string $crtCertbotDomain;
   private string $crtCertbotEmail;

   // SSH fields

   private string $sshHost;

   private string $sshPort;

   private string $sshPrivateKey;

   private string $sshRemoteUser;

   // RKE2 fields

   private string $rke2Kubeconfig;
   private string $rke2ClusterName;

   // IaC fields

   private string $iacInventoryCache;
   private string $iacInventoryName;
   private string $iacInventoryRemote;



    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
        // Required fields
        string $configmapId,
        string $owner,
        string $configMapName,
        // AWS fields
        string $awsAccessKeyId,
        string $awsSecretAccessKey,
        string $awsDefaultRegion,
        string $eksClusterName,
        // Azure fields
        string $azureSubscriptionId,
        string $azureClientId,
        string $azureSecret,
        string $azureTenant,
        string $azureResourceGroup,
        string $azureStorageAccount,
        string $azureStorageAccessKey,
        string $azureStorageConnectionString,
        string $azureIni,
        string $azureConf,
        // Docker Hub fields
        string $dockerhubUsername,
        string $dockerhubPassword,
        string $dockerhubNamespace,
        string $dockerhubPrivateNamespace,
        // SCM Git fields
        string $scmGitProtocol,
        string $scmGitFqdn,
        string $scmGitNamespace,
        string $scmGitUsername,
        string $scmGitPassword,
        string $scmGitAccessToken,
        // Cert Certbot fields
        string $crtCertbotCache,
        string $crtCertbotDomain,
        string $crtCertbotEmail,
        // SSH fields
        string $sshHost,
        string $sshPort,
        string $sshPrivateKey,
        string $sshRemoteUser,
        // RKE2 fields
        string $rke2Kubeconfig,
        string $rke2ClusterName,
        // IaC fields
        string $iacInventoryCache,
        string $iacInventoryName,
        string $iacInventoryRemote



    )
    {
        // Required fields
        $this->configmapId = $configmapId;
        $this->owner = $owner;
        $this->configMapName = $configMapName;

        // AWS
        $this->awsAccessKeyId = $awsAccessKeyId;
        $this->awsSecretAccessKey = $awsSecretAccessKey;
        $this->awsDefaultRegion = $awsDefaultRegion;
        $this->eksClusterName = $eksClusterName;

        // Azure
        $this->azureIni = $azureIni;
        $this->azureResourceGroup = $azureResourceGroup;
        $this->azureSubscriptionId = $azureSubscriptionId;
        $this->azureTenant = $azureTenant;
        $this->azureStorageAccount = $azureStorageAccount;
        $this->azureStorageAccessKey = $azureStorageAccessKey;
        $this->azureStorageConnectionString = $azureStorageConnectionString;
        $this->azureConf = $azureConf;
        $this->azureSecret = $azureSecret;
        $this->azureClientId = $azureClientId;

        // Docker Hub
        $this->dockerhubUsername = $dockerhubUsername;
        $this->dockerhubPassword = $dockerhubPassword;
        $this->dockerhubNamespace = $dockerhubNamespace;
        $this->dockerhubPrivateNamespace = $dockerhubPrivateNamespace;

        // SCM GIT
        $this->scmGitProtocol = $scmGitProtocol;
        $this->scmGitFqdn = $scmGitFqdn;
        $this->scmGitNamespace = $scmGitNamespace;
        $this->scmGitUsername = $scmGitUsername;
        $this->scmGitPassword = $scmGitPassword;
        $this->scmGitAccessToken = $scmGitAccessToken;

        // CRT Certbot
        $this->crtCertbotCache = $crtCertbotCache;
        $this->crtCertbotDomain = $crtCertbotDomain;
        $this->crtCertbotEmail = $crtCertbotEmail;

        // SSH
        $this->sshHost = $sshHost;
        $this->sshPort = $sshPort;
        $this->sshPrivateKey = $sshPrivateKey;
        $this->sshRemoteUser = $sshRemoteUser;

        // RKE2
        $this->rke2Kubeconfig = $rke2Kubeconfig;
        $this->rke2ClusterName = $rke2ClusterName;

        // IaC
        $this->iacInventoryCache = $iacInventoryCache;
        $this->iacInventoryName = $iacInventoryName;
        $this->iacInventoryRemote = $iacInventoryRemote;


        $data = [];

        // Required fields
        $data['configmap_id'] = $configmapId;
        $data['owner'] = $owner;
        $data['configmap_name'] = $configMapName;

        // AWS
        $data['aws_access_key_id'] = $awsAccessKeyId;
        $data['aws_secret_access_key'] = $awsSecretAccessKey;
        $data['aws_default_region'] = $awsDefaultRegion;
        $data['eks_cluster_name'] = $eksClusterName;

        // Azure
        $data['azure_subscription_id'] = $azureSubscriptionId;
        $data['azure_client_id'] = $azureClientId;
        $data['azure_secret'] = $azureSecret;
        $data['azure_tenant'] = $azureTenant;
        $data['azure_resource_group'] = $azureResourceGroup;
        $data['azure_storage_account'] = $azureStorageAccount;
        $data['azure_storage_access_key'] = $azureStorageAccessKey;
        $data['azure_storage_connection_string'] = $azureStorageConnectionString;
        $data['azure_ini'] = $azureIni;
        $data['azure_conf'] = $azureConf;

        // Docker Hub
        $data['dockerhub_username'] = $dockerhubUsername;
        $data['dockerhub_password'] = $dockerhubPassword;
        $data['dockerhub_namespace'] = $dockerhubNamespace;
        $data['dockerhub_private_namespace'] = $dockerhubPrivateNamespace;

        // SCM GIT
        $data['scm_git_protocol'] = $scmGitProtocol;
        $data['scm_git_fqdn'] = $scmGitFqdn;
        $data['scm_git_namespace'] = $scmGitNamespace;
        $data['scm_git_username'] = $scmGitUsername;
        $data['scm_git_password'] = $scmGitPassword;
        $data['scm_git_access_token'] = $scmGitAccessToken;

        // CRT Certbot
        $data['crt_certbot_cache'] = $crtCertbotCache;
        $data['crt_certbot_domain'] = $crtCertbotDomain;
        $data['crt_certbot_email'] = $crtCertbotEmail;

        // SSH
        $data['ssh_host'] = $sshHost;
        $data['ssh_port'] = $sshPort;
        $data['ssh_private_key'] = $sshPrivateKey;
        $data['ssh_remote_user'] = $sshRemoteUser;

        // RKE2
        $data['rke2_kubeconfig'] = $rke2Kubeconfig;
        $data['rke2_cluster_name'] = $rke2ClusterName;

        // IaC
        $data['iac_inventory_cache'] = $iacInventoryCache;
        $data['iac_inventory_name'] = $iacInventoryName;
        $data['iac_inventory_remote'] = $iacInventoryRemote;


        $this->setData($data);
    }



    // Required fields Getters

    public function getConfigmapId() : string
    {
        return $this->configmapId;
    }

    public function getOwner() : string
    {
        return $this->owner;
    }

    public function getConfigMapName() : string
    {
        return $this->configMapName;
    }


    // AWS Getters

    public function getAwsAccessKeyId() : string
    {
        return $this->awsAccessKeyId;
    }

    public function getAwsSecretAccessKey() : string
    {
        return $this->awsSecretAccessKey;
    }

    public function getAwsDefaultRegion() : string
    {
        return $this->awsDefaultRegion;
    }

    public function getEksClusterName() : string
    {
        return $this->eksClusterName;
    }


    // Azure Getters

    public function getAzureSubscriptionId() : string
    {
        return $this->azureSubscriptionId;
    }

    public function getAzureClientId() : string
    {
        return $this->azureClientId;
    }

    public function getAzureSecret() : string
    {
        return $this->azureSecret;
    }

    public function getAzureTenant() : string
    {
        return $this->azureTenant;
    }

    public function getAzureResourceGroup() : string
    {
        return $this->azureResourceGroup;
    }

    public function getAzureStorageAccount() : string
    {
        return $this->azureStorageAccount;
    }

    public function getAzureStorageAccessKey() : string
    {
        return $this->azureStorageAccessKey;
    }

    public function getAzureStorageConnectionString() : string
    {
        return $this->azureStorageConnectionString;
    }

    public function getAzureIni() : string
    {
        return $this->azureIni;
    }

    public function getAzureConf() : string
    {
        return $this->azureConf;
    }


    // Docker Hub Getters

    public function getDockerhubUsername() : string
    {
        return $this->dockerhubUsername;
    }

    public function getDockerhubPassword() : string
    {
        return $this->dockerhubPassword;
    }

    public function getDockerhubNamespace() : string
    {
        return $this->dockerhubNamespace;
    }

    public function getDockerhubPrivateNamespace() : string
    {
        return $this->dockerhubPrivateNamespace;
    }


    // SCM GIT Getters

    public function getScmGitProtocol() : string
    {
        return $this->scmGitProtocol;
    }

    public function getScmGitFqdn() : string
    {
        return $this->scmGitFqdn;
    }

    public function getScmGitNamespace() : string
    {
        return $this->scmGitNamespace;
    }

    public function getScmGitUsername() : string
    {
        return $this->scmGitUsername;
    }

    public function getScmGitPassword() : string
    {
        return $this->scmGitPassword;
    }

    public function getScmGitAccessToken() : string
    {
        return $this->scmGitAccessToken;
    }


    // CRT Certbot Getters

    public function getCrtCertbotCache() : string
    {
        return $this->crtCertbotCache;
    }

    public function getCrtCertbotDomain() : string
    {
        return $this->crtCertbotDomain;
    }
    public function getCrtCertbotEmail() : string
    {
        return $this->crtCertbotEmail;
    }


    // SSH Getters

    public function getSshHost() : string
    {
        return $this->sshHost;
    }

    public function getSshPort() : string
    {
        return $this->sshPort;
    }

    public function getSshPrivateKey() : string
    {
        return $this->sshPrivateKey;
    }

    public function getSshRemoteUser() : string
    {
        return $this->sshRemoteUser;
    }


    // RKE2 Getters

    public function getRke2Kubeconfig(): string
    {
        return $this->rke2Kubeconfig;
    }

    public function getRke2ClusterName(): string
    {
        return $this->rke2ClusterName;
    }


    // IaC Getters

    public function getIacInventoryCache() : string
    {
        return $this->iacInventoryCache;
    }

    public function getIacInventoryName() : string
    {
        return $this->iacInventoryName;
    }

    public function getIacInventoryRemote() : string
    {
        return $this->iacInventoryRemote;
    }






}