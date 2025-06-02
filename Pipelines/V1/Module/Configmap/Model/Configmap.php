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

    private string $configmapId;

   private string $owner;

   private string $configMapName;

   private string $awsAccessKeyId;

   private string $awsSecretAccessKey;

   private string $awsDefaultRegion;

    private string $eksClusterName;

   private string $azureIniDemoWeb;
   private string $azureIniDemoInfra;

   private string $azureResourceGroup;

   private string $azureSubscriptionId;

   private string $azureTenant;

   private string $azureStorageAccount;

   private string $azureStorageAccessKey;

   private string $azureStorageConnectionString;

   private string $azureConf;

   private string $azureSecret;

   private string $azureClientId;

   private string $dockerhubUsername;
   private string $dockerhubPassword;

   private string $dockerhubNamespace;
   private string $dockerhubPrivateNamespace;

   private string $scmGitNamespace;

   private string $scmGitProtocol;

   private string $scmGitProvider;
   private string $scmPrivateGitProvider;
   private string $scmGitAccessToken;

   private string $scmGitPassword;

   private string $scmGitUsername;

   private string $crtCertbotCache;
   private string $crtCertbotDomain;
   private string $crtCertbotEmail;

   private string $sshHost;

   private string $sshPort;

   private string $sshPrivateKey;

   private string $sshRemoteUser;

   private string $rke2Kubeconfig;
   private string $rke2ClusterName;

   private string $iacInventoryCache;
   private string $iacInventoryName;
   private string $iacInventoryRemote;



    private function __construct(array $data = [])
    {
        parent::__construct($data);
    }

    public function __invoke(
        string $configmapId,
        string $owner,
        string $configMapName,
        string $awsAccessKeyId,
        string $awsSecretAccessKey,
        string $awsDefaultRegion,
        string $eksClusterName,
        string $azureIniDemoWeb,
        string $azureIniDemoInfra,
        string $azureResourceGroup,
        string $azureSubscriptionId,
        string $azureTenant,
        string $azureStorageAccount,
        string $azureStorageAccessKey,
        string $azureStorageConnectionString,
        string $azureConf,
        string $azureSecret,
        string $azureClientId,
        string $dockerhubUsername,
        string $dockerhubPassword,
        string $dockerhubNamespace,
        string $dockerhubPrivateNamespace,
        string $scmGitNamespace,
        string $scmGitProtocol,
        string $scmGitProvider,
        string $scmPrivateGitProvider,
        string $scmGitAccessToken,
        string $scmGitPassword,
        string $scmGitUsername,
        string $crtCertbotCache,
        string $crtCertbotDomain,
        string $crtCertbotEmail,
        string $sshHost,
        string $sshPort,
        string $sshPrivateKey,
        string $sshRemoteUser,
        string $rke2Kubeconfig,
        string $rke2ClusterName,
        string $iacInventoryCache,
        string $iacInventoryName,
        string $iacInventoryRemote



    )
    {
        $this->configmapId = $configmapId;
        $this->owner = $owner;
        $this->configMapName = $configMapName;
        $this->awsAccessKeyId = $awsAccessKeyId;
        $this->awsSecretAccessKey = $awsSecretAccessKey;
        $this->awsDefaultRegion = $awsDefaultRegion;
        $this->eksClusterName = $eksClusterName;
        $this->azureIniDemoWeb = $azureIniDemoWeb;
        $this->azureIniDemoInfra = $azureIniDemoInfra;
        $this->azureResourceGroup = $azureResourceGroup;
        $this->azureSubscriptionId = $azureSubscriptionId;
        $this->azureTenant = $azureTenant;
        $this->azureStorageAccount = $azureStorageAccount;
        $this->azureStorageAccessKey = $azureStorageAccessKey;
        $this->azureStorageConnectionString = $azureStorageConnectionString;
        $this->azureConf = $azureConf;
        $this->azureSecret = $azureSecret;
        $this->azureClientId = $azureClientId;
        $this->dockerhubUsername = $dockerhubUsername;
        $this->dockerhubPassword = $dockerhubPassword;
        $this->dockerhubNamespace = $dockerhubNamespace;
        $this->dockerhubPrivateNamespace = $dockerhubPrivateNamespace;
        $this->scmGitNamespace = $scmGitNamespace;
        $this->scmGitProtocol = $scmGitProtocol;
        $this->scmGitProvider = $scmGitProvider;
        $this->scmPrivateGitProvider = $scmPrivateGitProvider;
        $this->scmGitAccessToken = $scmGitAccessToken;
        $this->scmGitPassword = $scmGitPassword;
        $this->scmGitUsername = $scmGitUsername;
        $this->crtCertbotCache = $crtCertbotCache;
        $this->crtCertbotDomain = $crtCertbotDomain;
        $this->crtCertbotEmail = $crtCertbotEmail;
        $this->sshHost = $sshHost;
        $this->sshPort = $sshPort;
        $this->sshPrivateKey = $sshPrivateKey;
        $this->sshRemoteUser = $sshRemoteUser;
        $this->rke2Kubeconfig = $rke2Kubeconfig;
        $this->rke2ClusterName = $rke2ClusterName;
        $this->iacInventoryCache = $iacInventoryCache;
        $this->iacInventoryName = $iacInventoryName;
        $this->iacInventoryRemote = $iacInventoryRemote;



        $data = [];

        $data['configmap_id'] = $configmapId;
        $data['owner'] = $owner;
        $data['configmap_name'] = $configMapName;
        $data['aws_access_key_id'] = $awsAccessKeyId;
        $data['aws_secret_access_key'] = $awsSecretAccessKey;
        $data['aws_default_region'] = $awsDefaultRegion;
        $data['eksClusterName'] = $eksClusterName;
        $data['azure_ini_demo_web'] = $azureIniDemoWeb;
        $data['azure_ini_demo_infra'] = $azureIniDemoInfra;
        $data['azure_resource_group'] = $azureResourceGroup;
        $data['azure_subscription_id'] = $azureSubscriptionId;
        $data['azure_tenant'] = $azureTenant;
        $data['azure_storage_account'] = $azureStorageAccount;
        $data['azure_storage_access_key'] = $azureStorageAccessKey;
        $data['azure_storage_connection_string'] = $azureStorageConnectionString;
        $data['azure_conf'] = $azureConf;
        $data['azure_secret'] = $azureSecret;
        $data['azure_client_id'] = $azureClientId;
        $data['dockerhub_username'] = $dockerhubUsername;
        $data['dockerhub_password'] = $dockerhubPassword;
        $data['dockerhub_namespace'] = $dockerhubNamespace;
        $data['dockerhub_private_namespace'] = $dockerhubPrivateNamespace;
        $data['scm_git_namespace'] = $scmGitNamespace;
        $data['scm_git_protocol'] = $scmGitProtocol;
        $data['scm_git_provider'] = $scmGitProvider;
        $data['scm_private_git_provider'] = $scmPrivateGitProvider;
        $data['scm_git_access_token'] = $scmGitAccessToken;
        $data['scm_git_password'] = $scmGitPassword;
        $data['scm_git_username'] = $scmGitUsername;
        $data['crt_certbot_cache'] = $crtCertbotCache;
        $data['crt_certbot_domain'] = $crtCertbotDomain;
        $data['crt_certbot_email'] = $crtCertbotEmail;
        $data['ssh_host'] = $sshHost;
        $data['ssh_port'] = $sshPort;
        $data['ssh_private_key'] = $sshPrivateKey;
        $data['ssh_remote_user'] = $sshRemoteUser;
        $data['rke2_kubeconfig'] = $rke2Kubeconfig;
        $data['rke2_cluster_name'] = $rke2ClusterName;
        $data['iacInventoryCache'] = $iacInventoryCache;
        $data['iacInventoryName'] = $iacInventoryName;
        $data['iacInventoryRemote'] = $iacInventoryRemote;



        $this->setData($data);
    }

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

    public function getAzureIniDemoWeb() : string
    {
        return $this->azureIniDemoWeb;
    }

    public function getAzureIniDemoInfra() : string
    {
        return $this->azureIniDemoInfra;
    }

    public function getAzureResourceGroup() : string
    {
        return $this->azureResourceGroup;
    }

    public function getAzureSubscriptionId() : string
    {
        return $this->azureSubscriptionId;
    }

    public function getAzureTenant() : string
    {
        return $this->azureTenant;
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

    public function getAzureConf() : string
    {
        return $this->azureConf;
    }

    public function getAzureSecret() : string
    {
        return $this->azureSecret;
    }

    public function getAzureClientId() : string
    {
        return $this->azureClientId;
    }

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

    public function getScmGitNamespace() : string
    {
        return $this->scmGitNamespace;
    }

    public function getScmGitProtocol() : string
    {
        return $this->scmGitProtocol;
    }

    public function getScmGitProvider() : string
    {
        return $this->scmGitProvider;
    }

    public function getScmPrivateGitProvider() : string
    {
        return $this->scmPrivateGitProvider;
    }

    public function getScmGitAccessToken() : string
    {
        return $this->scmGitAccessToken;
    }


    public function getScmGitPassword() : string
    {
        return $this->scmGitPassword;
    }

    public function getScmGitUsername() : string
    {
        return $this->scmGitUsername;
    }

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

    public function getRke2Kubeconfig(): string
    {
        return $this->rke2Kubeconfig;
    }

    public function getRke2ClusterName(): string
    {
        return $this->rke2ClusterName;
    }

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