<template>
    <div class="container">
        <div id="status" style="color: red;"></div>
        <div id="terminal"></div>
    </div>
</template>

<script>


export default {
    props:
        ['xdev'],

    mounted() {
        console.log('Component mounted.')
    }
}
</script>


<style>
    @import "/static/css/xterm.min.css";
    @import "/static/css/fullscreen.min.css";

    /*@import '~xterm/css/xterm.css';*/

    /*.row {*/
    /*    margin-top: 15px;*/
    /*    margin-bottom: 10px;*/
    /*}*/

    /*.container {*/
    /*    margin-top: 20px;*/
    /*}*/

    /*.btn {*/
    /*    margin-top: 15px;*/
    /*}*/

    /*.btn-danger {*/
    /*    margin-left: 5px;*/
    /*}*/

    /*{% if font.family %}*/
    /*@font-face {*/
    /*    font-family: '{{ font.family }}';*/
    /*    src: url('{{ font.url }}');*/
    /*}*/

    /*body {*/
    /*    font-family: '{{ font.family }}';*/
    /*}*/

    /*.xterm.fullscreen {*/
    /*    position:fixed;*/
    /*    top:0;*/
    /*    bottom:0;*/
    /*    left:0;*/
    /*    right:0;*/
    /*    width:auto;*/
    /*    height:auto;*/
    /*    z-index:255*/
    /*}*/
</style>
