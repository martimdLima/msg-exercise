<template>
  <div id="app">
    <header>
      <h1>Mortality Table Viewer</h1>
    </header>
    <main>
      <MortalityTable />
    </main>
  </div>
</template>

<script>
import MortalityTable from './components/MortalityTable.vue';

export default {
  name: 'App',
  components: {
    MortalityTable
  }
};
</script>

<style>
/* Add global styles or BootstrapVue CSS imports here */
</style>