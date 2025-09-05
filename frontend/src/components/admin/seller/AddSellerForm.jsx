import React, { useState } from "react";
import { useDispatch } from "react-redux";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import InputField from "../../common/InputField";
import Spinners from "../../common/Spinner";
import { createSeller } from "../../../store/seller-slice";

const AddSellerForm = ({ setOpen }) => {
  const dispatch = useDispatch();
  const [loader, setLoader] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    mode: "onTouched",
  });

  const addSellerHandler = (data) => {
    const sendData = {
      username: data.username,
      email: data.email,
      password: data.password,
      roles: ["seller"],
    };

    setLoader(true);
    dispatch(createSeller(sendData))
      .unwrap()
      .then((res) => {
        toast.success(res.message || "Seller created successfully");
        reset();
        setOpen(false);
      })
      .catch((err) => {
        toast.error(err.message || "Seller create failed");
      })
      .finally(() => {
        setLoader(false);
      });
  };

  return (
    <div className="py-5 relative h-full ">
      <form className="space-y-4 " onSubmit={handleSubmit(addSellerHandler)}>
        <div className="flex  flex-col gap-4 w-full">
          <InputField
            label="UserName"
            required
            id="username"
            type="text"
            message="*UserName is required"
            placeholder="Enter your username"
            register={register}
            errors={errors}
          />
          <InputField
            label="Email"
            required
            id="email"
            type="email"
            message="*Email is required"
            placeholder="Enter your email"
            register={register}
            errors={errors}
          />
          <InputField
            label="Password"
            required
            id="password"
            type="password"
            message="*Password is required"
            placeholder="Enter your password"
            register={register}
            errors={errors}
          />
        </div>

        <div className="flex  w-full justify-between items-center absolute bottom-14">
          <button
            disabled={loader}
            onClick={() => setOpen(false)}
            type="button"
            className={`border border-blue-500 rounded-[5px] font-metropolis  text-textColor py-[10px] px-4 text-sm font-medium`}
          >
            Cancel
          </button>
          <button
            disabled={loader}
            type="submit"
            className={`font-metropolis rounded-[5px]  bg-custom-blue hover:bg-blue-800 text-white  py-[10px] px-4 text-sm font-medium`}
          >
            {loader ? (
              <div className="flex gap-2 items-center">
                <Spinners /> Loading..
              </div>
            ) : (
              "Add New Seller"
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddSellerForm;
