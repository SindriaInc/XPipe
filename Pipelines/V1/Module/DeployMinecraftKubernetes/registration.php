<?php
use Magento\Framework\Component\ComponentRegistrar;

ComponentRegistrar::register(
    ComponentRegistrar::MODULE,
    'Pipelines_DeployMinecraftKubernetes',
    __DIR__
);
