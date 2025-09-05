import React, { useEffect, useState } from "react";
import InputField from "../common/InputField";
import Spinners from "../common/Spinner";
import { useForm } from "react-hook-form";
import { FaAddressCard } from "react-icons/fa";
import toast from "react-hot-toast";
import { createAddress, getAllAddresses, updateAddress } from "../../store/address-slice";
import { useDispatch } from "react-redux";

const AddAddressForm = ({ address, setOpenAddressModal }) => {
  const [loader, setLoader] = useState(false);
  const dispatch = useDispatch();

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({ mode: "onTouched" });

  const saveAddressHandler = async (data) => {
    if (data.id) {
      setLoader(true);
      try {
        await dispatch(updateAddress(data)).unwrap();
        toast.success("Updated address successfully");
        //   await dispatch(getAllAddresses(data)).unwrap();
        setOpenAddressModal(false);
        reset();
      } catch (err) {
        toast.error(
          typeof err === "string" ? err : err?.message || "Update address failed"
        );
      } finally {
        setLoader(false);
      }
    } else {
      setLoader(true);
      try {
        await dispatch(createAddress(data)).unwrap();
        toast.success("Added address successfully");
        //   await dispatch(getAllAddresses(data)).unwrap();
        reset();
      } catch (err) {
        toast.error(
          typeof err === "string" ? err : err?.message || "Add address failed"
        );
      } finally {
        setLoader(false);
      }
    }
  };

  useEffect(() => {
    if (address?.id) {
      setValue("id", address?.id);
      setValue("fullName", address?.fullName);
      setValue("phoneNumber", address?.phoneNumber);
      setValue("streetAddress", address?.streetAddress);
      setValue("ward", address?.ward);
      setValue("city", address?.city);
    }
  }, [address]);

  return (
    <div className="">
      <form onSubmit={handleSubmit(saveAddressHandler)} className="">
        <div className="flex justify-center items-center mb-4 font-semibold text-2xl text-slate-800 py-2 px-4">
          <FaAddressCard className="mr-2 text-2xl" />
          {!address?.id ? "Add Address" : "Update Address"}
        </div>

        <div className="flex flex-col gap-4">
          <InputField
            label="Fullname"
            required
            id="fullName"
            type="text"
            message="*Fullname is required"
            placeholder="Enter fullname"
            register={register}
            errors={errors}
          />

          <InputField
            label="Phone-number"
            required
            id="phoneNumber"
            type="text"
            placeholder="Enter phone number"
            message="*Phone number is required"
            register={register}
            errors={errors}
          />

          <InputField
            label="Street-address"
            required
            id="streetAddress"
            type="text"
            message="*Street address is required"
            placeholder="Enter street address"
            register={register}
            errors={errors}
          />

          <InputField
            label="ward"
            required
            id="ward"
            type="text"
            message="*Ward is required"
            placeholder="Enter ward"
            register={register}
            errors={errors}
          />

          <InputField
            label="City"
            required
            id="city"
            type="text"
            message="*City is required"
            placeholder="Enter city"
            register={register}
            errors={errors}
          />
        </div>

        <button
          disabled={loader}
          className="text-white bg-custom-blue px-4 py-2 rounded-md mt-4"
          type="submit"
        >
          {loader ? (
            <>
              <Spinners /> Loading...
            </>
          ) : (
            <>Save</>
          )}
        </button>
      </form>
    </div>
  );
};

export default AddAddressForm;
