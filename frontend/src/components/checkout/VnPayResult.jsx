import React, { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { useDispatch } from "react-redux";
import { clearCart, removeAllItemsFromCartServer, removeFromCartServer } from "../../store/cart-slice";

const VnPayResult = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch(); 

  useEffect(() => {
    const query = new URLSearchParams(location.search);
    const status = query.get("status");

    if (status === "success") {
      toast.success("Payment successfully!");
      dispatch(removeAllItemsFromCartServer());
      dispatch(clearCart()); 
    } else if (status === "failed") {
      toast.error("Payment failed");
    } else {
      toast.error("Invalid payment result");
    }

    // Sau 3s quay về trang chủ
    const timeout = setTimeout(() => {
      navigate("/");
    }, 3000);

    return () => clearTimeout(timeout);
  }, [location, navigate, dispatch]);

  return (
    <div className="flex flex-col items-center justify-center min-h-[300px]">
      <p className="text-lg font-semibold">Processing payment result...</p>
      <p className="text-gray-500 mt-2">You will be redirected shortly.</p>
    </div>
  );
};

export default VnPayResult;
