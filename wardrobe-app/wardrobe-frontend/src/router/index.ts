import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import HomeView from '../views/HomeView.vue';
import LoginView from '../views/LoginView.vue';
import RegisterView from '../views/RegisterView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', name: 'home', component: HomeView },
        { path: '/login', name: 'login', component: LoginView, meta: { guest: true } },
        { path: '/register', name: 'register', component: RegisterView, meta: { guest: true } },

        {
            path: '/wardrobe',
            name: 'wardrobe',
            component: () => import('../views/WardrobeView.vue'),
            meta: { requiresAuth: true }
        },

        {
            path: '/outfits',
            name: 'outfits',
            component: () => import('../views/OutfitsView.vue'),
            meta: { requiresAuth: true }
        },

        {
            path: '/outfits/create',
            name: 'outfit-create',
            component: () => import('../views/OutfitConstructorView.vue'),
            meta: { requiresAuth: true }
        },

        {
            path: '/outfits/:id',
            name: 'outfit-edit',
            component: () => import('../views/OutfitConstructorView.vue'),
            meta: { requiresAuth: true }
        },

        {
            path: '/profile',
            name: 'profile',
            component: () => import('../views/ProfileView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/admin',
            name: 'admin',
            component: () => import('../views/AdminView.vue'),
            meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
            path: '/ootd',
            name: 'ootd',
            component: () => import('../views/OotdView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/calendar',
            name: 'calendar',
            component: () => import('../views/CalendarView.vue'),
            meta: { requiresAuth: true }
        },
    ],
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next('/login');
    }
    else if (to.meta.requiresAdmin && !authStore.isAdmin) {
        next('/wardrobe');
    }
    else if (to.meta.guest && authStore.isAuthenticated) {
        next('/ootd');
    }
    else {
        next();
    }
});

export default router;
