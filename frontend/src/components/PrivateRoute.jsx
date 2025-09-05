import React from 'react'
import { useSelector } from 'react-redux'
import { Navigate, Outlet, useLocation } from 'react-router-dom';

const PrivateRoute = ({ publicPage = false, adminOnly = false }) => {
    const { userResponse } = useSelector((state) => state.auth);
    const isAdmin = userResponse?.roles?.includes("ADMIN");
    const isSeller = userResponse?.roles?.includes("SELLER");
    const location = useLocation();

    // 1. Public pages (login, register)
    if (publicPage) {
        return userResponse ? <Navigate to="/" /> : <Outlet />
    }

    // 2. Admin pages
    if (adminOnly) {
        if (!userResponse) return <Navigate to="/login" state={{ from: location }} replace />;

        if (isSeller && !isAdmin) {
            const sellerAllowedPaths = ["/admin/orders", "/admin/products"];
            const sellerAllowed = sellerAllowedPaths.some(path =>
                location.pathname.startsWith(path)
            );
            if (!sellerAllowed) {
                return <Navigate to="/" replace />
            }
        }

        if (!isAdmin && !isSeller) {
            return <Navigate to="/" replace />
        }
        return <Outlet />
    }

    // 3. Các route private bình thường (ví dụ /checkout)
    return userResponse ? <Outlet /> : <Navigate to="/login" state={{ from: location }} replace />;
}

export default PrivateRoute;
