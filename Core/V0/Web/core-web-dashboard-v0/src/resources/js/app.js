/**
 * First we will load all of this project's JavaScript dependencies which
 * includes Vue and other libraries. It is a great starting point when
 * building robust, powerful web applications using Vue and Laravel.
 */

require('./bootstrap');

// Vue 2.x
window.Vue = require('vue');

// import Vue from 'vue'
// import VueCompositionAPI from '@vue/composition-api'
//
// Vue.use(VueCompositionAPI)

// Vue 3.x
//import { createApp } from 'vue';
//import HelloVue from './components/HelloVue.vue';

/**
 * The following block of code may be used to automatically register your
 * Vue components. It will recursively scan this directory for the Vue
 * components and automatically register them with their "basename".
 *
 * Eg. ./components/ExampleComponent.vue -> <example-component></example-component>
 */

// const files = require.context('./', true, /\.vue$/i)
// files.keys().map(key => Vue.component(key.split('/').pop().split('.')[0], files(key).default))

// Vue 2.x
Vue.component('example-component', require('./components/ExampleComponent.vue').default);
Vue.component('web-terminal-component', require('./components/WebTerminalComponent.vue').default);
Vue.component('pipeline-log-component', require('./components/PipelineLogComponent.vue').default);
Vue.component('chat-component', require('./components/ChatComponent.vue').default);
Vue.component('xdev-gui-component', require('./components/XdevGuiComponent.vue').default);
Vue.component('xdev-cli-component', require('./components/XdevCliComponent.vue').default);

/**
 * Next, we will create a fresh Vue application instance and attach it to
 * the page. Then, you may begin adding components to this application
 * or customize the JavaScript scaffolding to fit your unique needs.
 */

// Vue 2.x
const app = new Vue({
    el: '#app',
});


// Vue 3.x
// createApp({
//     components: {
//         HelloVue,
//     }
// }).mount('#app');
