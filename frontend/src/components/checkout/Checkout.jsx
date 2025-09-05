import { Button, Step, StepLabel, Stepper } from "@mui/material";
import React, { useEffect, useState } from "react";
import AddressInfo from "./AddressInfo";
import { useDispatch, useSelector } from "react-redux";
import { getAllAddresses } from "../../store/address-slice";
import toast from "react-hot-toast";
import Skeleton from "../common/Skeleton";
import ErrorPage from "../common/ErrorPage";
import PaymentMethod from "./PaymentMethod";
import OrderSumary from "./OrderSumary";
import MomoPayment from "./MomoPayment";
import VNPayPayment from "./VNPayPayment";

const Checkout = () => {
  const dispatch = useDispatch();
  const [activeStep, setActiveStep] = useState(0);

  // NOTE[REMOVED]: không còn dùng loadingCreateOrder/redirectUrl vì Step 3 (VNPayPayment) lo gọi API và redirect
  // const [loadingCreateOrder, setLoadingCreateOrder] = useState(false);
  // const [redirectUrl, setRedirectUrl] = useState(null);

  const { addresses, loading, error } = useSelector((state) => state.address);
  const { items, totalPrice } = useSelector((state) => state.cart);
  const { selectedUserCheckoutAddress } = useSelector((state) => state.auth);
  const { paymentMethod } = useSelector((state) => state.paymentMethod);

  const steps = ["Address", "Payment Method", "Order Summary", "Payment"];

  const handleBack = () => {
    setActiveStep((prevStep) => prevStep - 1);
  };

  const handleNext = () => {
    if (activeStep === 0 && !selectedUserCheckoutAddress) {
      toast.error("Please select checkout address");
      return;
    }
    if (activeStep === 1 && (!selectedUserCheckoutAddress || !paymentMethod)) {
      // NOTE[CHANGED]: sửa message cho đúng nội dung
      toast.error("Please select payment method");
      return;
    }

    // NOTE[CHANGED]: KHÔNG gọi API ở Step 2; chỉ tăng bước để sang Step 3 (VNPayPayment sẽ tự gọi API)
    setActiveStep((prevStep) => prevStep + 1);
  };

  // NOTE[CHANGED]: helper nhận diện VNPay an toàn (tránh lệ thuộc "VNPay"/"VN_PAY")
  const isVNPay = String(paymentMethod || "").toUpperCase().includes("VNPAY");

  useEffect(() => {
    dispatch(getAllAddresses());
  }, [dispatch]);

  return (
    <div className="py-14 min-h-[calc(100vh-100px)]">
      <Stepper activeStep={activeStep} alternativeLabel>
        {steps.map((label, index) => (
          <Step key={index}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      {loading ? (
        <div className="lg:w-[80%] mx-auto py-5">
          <Skeleton />
        </div>
      ) : (
        <div className="mt-5">
          {activeStep === 0 && <AddressInfo addresses={addresses} />}
          {activeStep === 1 && <PaymentMethod />}
          {activeStep === 2 && (
            <OrderSumary
              totalPrice={totalPrice}
              items={items}
              address={selectedUserCheckoutAddress}
              paymentMethod={paymentMethod}
            />
          )}
          {activeStep === 3 && (
            <>
              {isVNPay ? (
                // NOTE[CHANGED]: sang Step 3 mới render VNPayPayment; component này sẽ:
                // 1) gọi API tạo order
                // 2) hiển thị loading trong lúc gọi
                // 3) nhận redirectUrl thì window.location.href = redirectUrl
                <VNPayPayment />
              ) : (
                // Bạn có thể để Momo/COD xử lý tương tự tại đây
                <MomoPayment />
              )}
            </>
          )}
        </div>
      )}

      <div
        className="flex justify-between items-center px-4 fixed z-50 h-24 bottom-0 bg-white left-0 w-full py-4 border-slate-200"
        style={{ boxShadow: "0 -2px 4px rgba(100, 100, 100, 0.15" }}
      >
        <Button
          variant="outlined"
          disabled={activeStep === 0}
          onClick={handleBack}
        >
          Back
        </Button>

        {/* NOTE: Step 3 là bước cuối nên nút Proceed sẽ ẩn như code gốc của bạn */}
        {activeStep !== steps.length - 1 && (
          <button
            disabled={
              error ||
              (activeStep === 0
                ? !selectedUserCheckoutAddress
                : activeStep === 1
                ? !paymentMethod
                : false)
            }
            className={`bg-custom-blue font-semibold px-6 h-10 rounded-md text-white 
              ${
                error ||
                (activeStep === 0 && !selectedUserCheckoutAddress) ||
                (activeStep === 1 && !paymentMethod)
                  ? "opacity-60"
                  : ""
              }`}
            onClick={handleNext}
          >
            Proceed
          </button>
        )}
      </div>

      {error && <ErrorPage message={error} />}
    </div>
  );
};

export default Checkout;
