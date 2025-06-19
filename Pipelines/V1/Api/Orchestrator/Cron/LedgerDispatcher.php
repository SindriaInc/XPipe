<?php
namespace Pipelines\Orchestrator\Cron;

class LedgerDispatcher
{
    public function execute()
    {
        // Scansiona ledger dove dispatched_at è NULL e invia i messaggi in coda
        // Implementa transactional outbox pattern
    }
}
