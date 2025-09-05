import "./App.css";
import Home from "./components/home/Home";
import Products from "./components/products/Products";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/common/Navbar";
import About from "./components/About";
import Contact from "./components/Contact";
import Cart from "./components/cart/Cart";
import React from "react";
import { Toaster } from "react-hot-toast";
import LogIn from "./components/auth/Login";
import Register from "./components/auth/Register";
import PrivateRoute from "./components/PrivateRoute";
import Checkout from "./components/checkout/Checkout";
import AdminLayout from "./components/admin/AdminLayout";
import DashBoard from "./components/admin/dashboard/DashBoard";
import AdminProducts from "./components/admin/products/AdminProducts";
import Category from "./components/admin/categories/Category";
import Seller from "./components/admin/seller/Seller";
import VnPayResult from "./components/checkout/VnPayResult";
import Orders from "./components/admin/orders/Orders";
function App() {
  return (
    <React.Fragment>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/payment-result" element={<VnPayResult />} />

          <Route element={<PrivateRoute />}>
            <Route path="/checkout" element={<Checkout />} />
          </Route>

          <Route element={<PrivateRoute publicPage />}>
            <Route path="login" element={<LogIn />} />
            <Route path="/register" element={<Register />} />
          </Route>

          <Route path="/" element={<PrivateRoute adminOnly />}>
            <Route path="/admin" element={<AdminLayout />} > 
              <Route path="" element={<DashBoard />} />
              <Route path="products" element={<AdminProducts />} />
              <Route path="sellers" element={<Seller />} />
              <Route path="categories" element={<Category />} />
              <Route path="orders" element={<Orders />} />

            </Route>
          </Route>

        </Routes>
      </Router>
      <Toaster position="bottom-center" />
    </React.Fragment>
  );
}

export default App;
