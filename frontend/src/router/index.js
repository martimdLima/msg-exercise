import Vue from 'vue';
import VueRouter from 'vue-router';
import MortalityTable from '../components/MortalityTable.vue';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'MortalityTable',
    component: MortalityTable,
  },
];

const router = new VueRouter({
  routes,
});

export default router;