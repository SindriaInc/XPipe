<?php

return [

    /**
     *  Copyright Sindria Inc.
     *
     *  Dashboard translation
     *
     */


    // Sidebar
    'sidebar.back'               => 'Torna alla Home',
    'sidebar.dashboard'          => 'Dashboard',
    'sidebar.pipelines'          => 'Pipelines',
    'sidebar.alerts'             => 'Allarmi',
    'sidebar.users'              => 'Utenti',
    'sidebar.policies'           => 'Permessi',
    'sidebar.settings'           => 'Impostazioni',


    // Dashboard
    'dashboard.title'   => 'Dashboard',

    // Widgets
    'widgets.main.welcome'  => 'Benvenuto',
    'widgets.main.text'     => 'Per ulteriori informazioni sull\'installazione e la configurazione, vedere la nostra documentazione.',
    'widgets.main.download' => 'Scarica Agente',

    // Pipelines
    'pipelines.title'                       => 'Pipelines',
    'pipelines.add'                         => 'Crea Pipeline',
    'pipelines.attach'                      => 'Allega Pipeline',
    'pipelines.edit'                        => 'Modifica Pipeline',
    'pipelines.field.info'                  => 'Info Pipeline',
    'pipelines.field.id'                    => 'ID',
    'pipelines.field.name'                  => 'Nome',
    'pipelines.field.content'               => 'Contenuto',
    'pipelines.field.created_at'            => 'Creato il',
    // Pipelines Search
    'pipelines.search.placeholder'          => 'Cerca pipeline...',
    // Pipelines Users
    'pipelines.users.title'                 => 'Utenti',
    'pipelines.users.search.placeholder'    => 'Cerca Utenti...',
    'pipelines.users.field.id'              => 'ID',
    'pipelines.users.field.username'        => 'Username',
    'pipelines.attach.field.user_id'        => 'Seleziona Utente',
    'pipelines.attach.field.pipeline_id'    => 'Seleziona Pipeline',

    // Users
    'users.title'                       => 'Utenti',
    'users.add'                         => 'Crea Utente',
    'users.edit'                        => 'Modifica Utente',
    'users.field.info'                  => 'Info Utente',
    'users.field.id'                    => 'ID',
    'users.field.username'              => 'Username',
    'users.field.email'                 => 'Email',
    'users.field.name'                  => 'Nome',
    'users.field.surname'               => 'Cognome',
    'users.field.status'                => 'Stato',
    'users.field.status.true'           => 'Attivo',
    'users.field.status.false'          => 'Disabilitato',
    'users.field.email_verified'        => 'Email Verificata',
    'users.field.email_verified.true'   => 'Si',
    'users.field.email_verified.false'  => 'No',
    'users.field.created_at'            => 'Creato il',
    // Users Search
    'users.search.placeholder'          => 'Cerca Utenti...',
    // Users Policies
    'users.policies.title'              => 'Permessi',
    'users.policies.search.placeholder' => 'Cerca Policy...',
    'users.policies.field.name'         => 'Nome',

    // Policies
    'policies.title'                       => 'Permessi',
    'policies.add'                         => 'Crea Policy',
    'policies.attach'                      => 'Allega Policy',
    'policies.edit'                        => 'Modifica Policy',
    'policies.field.info'                  => 'Info Policy',
    'policies.field.id'                    => 'ID',
    'policies.field.name'                  => 'Nome',
    'policies.field.type'                  => 'Tipologia',
    'policies.field.type.select'           => 'Seleziona Tipologia',
    'policies.field.content'               => 'Contenuto',
    'policies.field.created_at'            => 'Creato il',
    // Policies Search
    'policies.search.placeholder'          => 'Cerca policy...',
    // Policies Users
    'policies.users.title'                 => 'Utenti',
    'policies.users.search.placeholder'    => 'Cerca Utenti...',
    'policies.users.field.id'              => 'ID',
    'policies.users.field.username'        => 'Username',
    'policies.attach.field.user_id'        => 'Seleziona Utente',
    'policies.attach.field.policy_id'      => 'Seleziona Policy',


    // Tournaments Subscribers
    'users.subscribers.title'                    => 'Iscrizioni',
    'users.subscribers.add'                      => 'Aggiungi Partecipante',
    'users.subscribers.show'                     => 'Modifica Partecipante',
    'users.subscribers.edit'                     => 'Partecipante modificato con successo',
    'users.subscribers.list'                     => 'Lista Iscritti',
    'users.subscribers.store'                    => 'Partecipante aggiunto con successo',
    'users.subscribers.store.error'              => 'Partecipante già registrato',
    'users.subscribers.delete'                   => 'Partecipante eliminato con successo',
    'users.subscribers.field.code'               => 'Codice Partecipante',
    'users.subscribers.field.data'               => 'Dati anagrafici',
    'users.subscribers.field.name'               => 'Nome',
    'users.subscribers.field.surname'            => 'Cognome',
    'users.subscribers.field.birthday'           => 'Data di nascita',
    'users.subscribers.field.email'              => 'Email',
    'users.subscribers.field.phone'              => 'Telefono',
    'users.subscribers.field.info'               => 'Info Partecipante',
    'users.subscribers.field.fit'                => 'Tessera Fit',
    'users.subscribers.field.club'               => 'Circolo',
    'users.subscribers.field.score'              => 'Classifica',
    'users.subscribers.field.category'           => 'Categoria',
    'users.subscribers.field.type'               => 'Iscritto come',
    'users.subscribers.field.note'               => 'Note',
    'users.subscribers.field.note.optional'      => 'es. informazioni aggiuntive e/o richieste',
    'users.subscribers.field.users'              => 'Tornei',
    // users.subscribers search
    'users.subscribers.search.placeholder'       => 'Cerca Iscritti...',


    // Settings
    'settings.title'                          => 'Impostazioni',
    'settings.field.users'                    => 'Impostazioni Tornei',
    'settings.field.users.subscriptions'      => 'Abilita/Disabilita Iscrizioni',


    // Modals
    'modals.warning.title' => 'Sei sicuro?',
    'modals.warning.p1'    => 'Questa operazione potrebbe distruggere tutti i dati dal database.',
    'modals.warning.p2'    => 'Si consiglia cautela.',
    'modals.warning.p3'    => 'Seleziona "Applica" per procedere.',
    'modals.upload.title'  => 'Carica file',
    'modals.logout.title'  => 'Sei sicuro?',
    'modals.logout.p1'     => 'Seleziona "Logout" se sei pronto per terminare la sessione corrente.',
    'modals.loader.title'  => 'Attendere...',
    'modals.loader.text'   => 'L\'operazione puo\' richiedere un po\' di tempo',
    // Modals Pipelines
    'modals.pipelines.delete.title' => 'Sei sicuro?',
    'modals.pipelines.delete.p1'    => 'Questa operazione distruggerà tutti i dati della pipeline',
    'modals.pipelines.delete.p2'    => 'Si consiglia cautela. Di norma la pipeline va eliminata solo se creata per errore.',
    'modals.pipelines.delete.p3'    => 'Seleziona "Conferma" per procedere.',
    // Modals Users
    'modals.users.delete.title' => 'Sei sicuro?',
    'modals.users.delete.p1'    => 'Questa operazione distruggerà tutti i dati dell\' utente',
    'modals.users.delete.p2'    => 'Si consiglia cautela. Di norma l\'utente va eliminato solo se creato per errore.',
    'modals.users.delete.p3'    => 'Seleziona "Conferma" per procedere.',
    // Modals Policies
    'modals.policies.delete.title' => 'Sei sicuro?',
    'modals.policies.delete.p1'    => 'Questa operazione distruggerà tutti i dati della policy',
    'modals.policies.delete.p2'    => 'Si consiglia cautela. Di norma la policy va eliminata solo se creata per errore.',
    'modals.policies.delete.p3'    => 'Seleziona "Conferma" per procedere.',
    // Modals Policies Detach
    'modals.policies.detach.title' => 'Sei sicuro?',
    'modals.policies.detach.p1'    => 'Questa operazione scolleghera\' la policy dall\'utente',
    'modals.policies.detach.p2'    => 'Si consiglia cautela.',
    'modals.policies.detach.p3'    => 'Seleziona "Conferma" per procedere.',
    // Modals Tournaments Archive
    'modals.users.archive.title' => 'Sei sicuro?',
    'modals.users.archive.p1'    => 'Questa operazione archiviera\' il torneo in modo permanente',
    'modals.users.archive.p2'    => 'Seleziona "Conferma" per procedere.',
    // Modals Tournaments Subscribers
    'modals.users.subscribers.delete.title' => 'Sei sicuro?',
    'modals.users.subscribers.delete.p1'    => 'Questa operazione distruggerà tutti i dati dell\'iscritto',
    'modals.users.subscribers.delete.p2'    => 'Seleziona "Conferma" per procedere.',


];
