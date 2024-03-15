<template>
    <div class="embed-responsive embed-responsive-21by9">
        <iframe class="embed-responsive-item" :src="iframe.url" @load="load" v-show="iframe.loaded" allowfullscreen></iframe>
    </div>
</template>

<script>

export default {
    props:
        ['xdev'],

    data() {
        return {
            iframe: {
                url: "http://localhost:8888/?command=pwd",
                loaded: false
            }
        }
    },

    methods: {
        load: function () {
            this.iframe.loaded = true;
        }
    },

    mounted() {
        console.log('Component mounted.')
    }
}
</script>
