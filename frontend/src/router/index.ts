import { createRouter, createWebHistory } from 'vue-router'
import IncidentCreateView from '../views/IncidentCreateView.vue'
import IncidentDetailView from '../views/IncidentDetailView.vue'
import IncidentListView from '../views/IncidentListView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/incidents' },
    { path: '/incidents', component: IncidentListView },
    { path: '/incidents/new', component: IncidentCreateView },
    { path: '/incidents/:id', component: IncidentDetailView, props: true },
  ],
})
