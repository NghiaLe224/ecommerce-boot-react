import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import { createOrder } from "../../store/order-slice";
import { Loader } from "../common/Loader";

const mapPaymentToEnum = (val) => {
  if (!val) return val;
  const s = String(val).toUpperCase();
  if (s === "VNPAY" || s === "VN_PAY") return "VNPAY";
  if (s === "MOMO") return "MOMO";
  if (s.includes("CASH")) return "CASH_ON_DELIVERY";
  return s;
};

const VNPayPayment = () => {
  const dispatch = useDispatch();
  const [loading, setLoading] = useState(false);

  const { items } = useSelector((state) => state.cart);
  const { selectedUserCheckoutAddress } = useSelector((state) => state.auth);
  const { paymentMethod } = useSelector((state) => state.paymentMethod);

  useEffect(() => {
    const run = async () => {
      setLoading(true);
      try {
        const body = {
          items: items.map((it) => ({
            productId: it.productId,
            quantity: it.quantity,
          })),
          shippingAddressId: selectedUserCheckoutAddress.id,
          paymentMethod: mapPaymentToEnum(paymentMethod),
          note: "Giao giờ hành chính nhé!",
        };

        const order = await dispatch(createOrder(body)).unwrap();
        console.log(order);
        
        const pay = order?.payment[0];

        console.log("pay: ", pay);
        console.log("gateway: " , pay.gatewayRedirectUrl);
        
        if (pay?.gatewayRedirectUrl) {
          window.location.href = pay.gatewayRedirectUrl; // sang cổng VNPay
        } else {
          toast.error("VNPay's payment link not found!");
        }
      } catch (e) {
        toast.error(typeof e === "string" ? e : "Create order failed");
      } finally {
        setLoading(false);
      }
    };

    // Gọi tạo order ngay khi vào Step 3
    run();
  }, [dispatch, items, selectedUserCheckoutAddress, paymentMethod]);

  return (
    <div className="flex items-center justify-center min-h-[220px]">
      {loading ? (
        // <p className="text-lg font-semibold text-blue-600">
        //   Creating order and redirect to VNPay...
        // </p>
        <Loader text='Creating order and redirect to VNPay...' />
      ) : (
        // <p className="text-lg">Redirecting...</p>
        <Loader text='Redirecting...' />
      )}
    </div>
  );
};

export default VNPayPayment;
