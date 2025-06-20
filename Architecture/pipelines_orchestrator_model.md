classDiagram
direction BT
class pipelines_orchestrator_ledger {
   int unsigned pipeline_id  /* Pipeline ID */
   varchar(32) status  /* Status */
   varchar(255) step  /* Execution Step */
   text message  /* Status Message */
   text payload  /* Execution Payload Snapshot */
   int retries  /* Retry Count */
   timestamp dispatched_at  /* Dispatch Timestamp */
   timestamp created_at  /* Created At */
   timestamp updated_at  /* Updated At */
   int unsigned ledger_id  /* Ledger ID */
}
class pipelines_orchestrator_pipeline {
   varchar(255) slug  /* Pipeline Slug */
   varchar(255) template  /* Pipeline Template */
   varchar(255) owner  /* Pipeline Owner */
   varchar(255) configmap_tenant  /* Configmap Tenant */
   varchar(255) configmap_name  /* Configmap Name */
   timestamp created_at  /* Created At */
   timestamp updated_at  /* Updated At */
   int unsigned pipeline_id  /* Pipeline ID */
}

node1  -->  node0 : pipeline_id
