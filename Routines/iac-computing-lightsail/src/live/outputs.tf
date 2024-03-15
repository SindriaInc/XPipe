output "resources" {
  value = {
    blueprints = module.lightsail-instance_blueprints.*
    instances = module.lightsail-instance_instances.*
    nodes = module.lightsail-instance_nodes.*
  }
}