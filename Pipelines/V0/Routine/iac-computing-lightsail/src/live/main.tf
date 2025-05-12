locals {
  common  = yamldecode(file("../config/common.yaml"))
  infra   = yamldecode(file("../config/infra.yaml"))
}

module "lightsail-instance_blueprints" {

  for_each = {
    for k, v in local.infra.blueprints : k => v
    if v.driver == "lightsail"
  }


  source                 = "../modules/lightsail-instance-module/lib"

  availability_zone      =  lookup("${each.value}", "zone", "${local.common.defaults.blueprints.lightsail.zone}")
  blueprint_id           =  lookup("${each.value}", "blueprint", "${local.common.defaults.blueprints.lightsail.blueprint}")
  bundle_id              =  lookup("${each.value}", "bundle", "${local.common.defaults.blueprints.lightsail.bundle}")

  name                   = "${each.value.name}"
  tags                   = merge("${local.common.tags}", "${each.value.tags}")

  pubkey_name            = "${local.common.cm.name}" == "" ? "" : "${local.common.cm.name}"
  pubkey_value           = "${local.common.cm.pubkey}" == "" ? "" : "${local.common.cm.pubkey}"

  namespace              = "${local.common.namespace}"
  email                  = "${local.common.email}"
}


module "lightsail-instance_instances" {

  for_each = {
    for k, v in local.infra.instances : k => v
    if v.driver == "lightsail"
  }

  source                 = "../modules/lightsail-instance-module/lib"

  availability_zone      =  lookup("${each.value}", "zone", "${local.common.defaults.instances.lightsail.zone}")
  blueprint_id           =  lookup("${each.value}", "blueprint", "${local.common.defaults.instances.lightsail.blueprint}")
  bundle_id              =  lookup("${each.value}", "bundle", "${local.common.defaults.instances.lightsail.bundle}")

  name                   = "${each.value.name}"
  tags                   = merge("${local.common.tags}", "${each.value.tags}")

  pubkey_name            = "${local.common.cm.name}" == "" ? "" : "${local.common.cm.name}"
  pubkey_value           = "${local.common.cm.pubkey}" == "" ? "" : "${local.common.cm.pubkey}"

  namespace              = "${local.common.namespace}"
  email                  = "${local.common.email}"
}


module "lightsail-instance_nodes" {

  for_each = {
    for k, v in local.infra.nodes : k => v
    if v.driver == "lightsail"
  }

  source                 = "../modules/lightsail-instance-module/lib"

  availability_zone      =  lookup("${each.value}", "zone", "${local.common.defaults.nodes.lightsail.zone}")
  blueprint_id           =  lookup("${each.value}", "blueprint", "${local.common.defaults.nodes.lightsail.blueprint}")
  bundle_id              =  lookup("${each.value}", "bundle", "${local.common.defaults.nodes.lightsail.bundle}")


  name                   = "${each.value.name}"
  tags                   = merge("${local.common.tags}", "${each.value.tags}")

  pubkey_name            = "${local.common.cm.name}" == "" ? "" : "${local.common.cm.name}"
  pubkey_value           = "${local.common.cm.pubkey}" == "" ? "" : "${local.common.cm.pubkey}"

  namespace              = "${local.common.namespace}"
  email                  = "${local.common.email}"
}