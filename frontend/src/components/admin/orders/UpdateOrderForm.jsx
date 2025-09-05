import {
  Button,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
} from "@mui/material";
import React, { useState } from "react";
import { FaSpinner } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import Spinners from "../../common/Spinner";
import { updateOrderStatusFromDashboard } from "../../../store/order-slice";

const ORDER_STATUSES = [
  "PENDING",
  "PAID",
  "CANCELLED",
  "SHIPPED",
  "DELIVERED",
  "REFUNDED",
];

const UpdateOrderForm = ({
  setOpen,
  selectedId,
  selectedItem,
  loader,
  setLoader,
}) => {
  const [orderStatus, setOrderStatus] = useState(
    selectedItem?.status || "Accepted"
  );
  const [error, setError] = useState("");
  const dispatch = useDispatch();

  const { userResponse } = useSelector((state) => state.auth);
  const isAdmin = userResponse && userResponse?.roles?.includes("ADMIN");

  const updateOrderStatus = (e) => {
    e.preventDefault();
    if (!orderStatus) {
      setError("Order status is required");
      return;
    }
    dispatch(
      updateOrderStatusFromDashboard({ orderId: selectedId, orderStatus, isAdmin })
    )
      .unwrap()
      .then((res) => {
        toast.success(res.message || "Update successfully");
        setOpen(false);
      })
      .catch((err) => {
        toast.error(err);
      })
      .finally(() => setLoader(false));
  };

  return (
    <div className="py-5 relative h-full">
      <form className="space-y-4" onSubmit={updateOrderStatus}>
        <FormControl fullWidth variant="outlined" error={!!error}>
          <InputLabel id="order-status-label">Order Status</InputLabel>
          <Select
            labelId="order-status-label"
            label="Order Status"
            value={orderStatus}
            onChange={(e) => {
              setOrderStatus(e.target.value);
              setError("");
            }}
          >
            {ORDER_STATUSES.map((status) => (
              <MenuItem key={status} value={status}>
                {status}
              </MenuItem>
            ))}
          </Select>

          {error && <FormHelperText>{error}</FormHelperText>}
        </FormControl>

        <div className="flex w-full justify-between items-center absolute bottom-14">
          <Button
            disabled={loader}
            onClick={() => setOpen(false)}
            variant="outlined"
            className="text-white py-[10px] px-4 text-sm font-medium"
          >
            Cancel
          </Button>

          <Button
            disabled={loader}
            type="submit"
            variant="contained"
            color="primary"
            className="bg-custom-blue text-white  py-[10px] px-4 text-sm font-medium"
          >
            {loader ? (
              <div className="flex gap-2 items-center">
                <Spinners /> Loading...
              </div>
            ) : (
              "Update"
            )}
          </Button>
        </div>
      </form>
    </div>
  );
};

export default UpdateOrderForm;
