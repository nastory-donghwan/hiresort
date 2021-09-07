
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ReservationManager from "./components/ReservationManager"

import PaymentManager from "./components/PaymentManager"

import IssuedManager from "./components/IssuedManager"


import View from "./components/View"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/reservations',
                name: 'ReservationManager',
                component: ReservationManager
            },

            {
                path: '/payments',
                name: 'PaymentManager',
                component: PaymentManager
            },

            {
                path: '/issueds',
                name: 'IssuedManager',
                component: IssuedManager
            },


            {
                path: '/views',
                name: 'View',
                component: View
            },


    ]
})
