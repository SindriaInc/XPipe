<template>
    <div class="embed-responsive embed-responsive-21by9">
        <iframe class="embed-responsive-item" :src="iframe.url" @load="load" v-show="iframe.loaded" allowfullscreen></iframe>
<!--        <vue-friendly-iframe className="embed-responsive-item" src="http://xdev-work.sindria.org:8080" allowfullscreen @load="onLoad"></vue-friendly-iframe>-->
    </div>
</template>

<script>
    //import Vue from 'vue';
    //import VueFriendlyIframe from 'vue-friendly-iframe';

    //Vue.use(VueFriendlyIframe);

    import VueSimpleWebSocket from 'vue-simple-websocket'



    export default {
        props:
        ['xdev'],

        data() {
          return {
              iframe: {
                  url: "http://xdev-work.sindria.org:8080/vnc.html?password=sindria&autoconnect=true&resize=remote",
                  loaded: false
              }
          }
        },

        methods: {
            load: function () {
                this.iframe.loaded = true;

                // Vue.use(VueSimpleWebSocket, 'ws://xdev-work.sindria.org:8080/websockify', {
                //     reconnectEnabled: true,
                //     reconnectInterval: 5000 // time to reconnect in milliseconds
                // })
            }
        },

        mounted() {
            console.log('Component mounted.')
        }
    }
</script>
