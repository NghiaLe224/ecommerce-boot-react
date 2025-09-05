import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useDispatch } from "react-redux";
import toast from "react-hot-toast";

import InputField from "../../common/InputField";
import {
  createCategoryDashboard,
  updateCategoryDashboard,
} from "../../../store/category-slice";

const AddCategoryForm = ({ setOpen, open, category, update = false }) => {
  const dispatch = useDispatch();

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm({
    mode: "onTouched",
  });

  const addNewCategoryHandler = (formData) => {
    if (!update) {
      dispatch(createCategoryDashboard(formData))
        .unwrap()
        .then(() => {
          toast.success("Category created successfully");
          reset();
          setOpen(false);
        })
        .catch((err) => {
          toast.error(
            typeof err === "string"
              ? err
              : err?.message || "Delete category failed"
          );
        });
    } else {
      dispatch(
        updateCategoryDashboard({ categoryId: category.id, data: formData })
      )
        .unwrap()
        .then(() => {
          toast.success("Category updated successfully");
          reset();
          setOpen(false);
        })
        .catch((err) => {
          toast.error(
            typeof err === "string"
              ? err
              : err?.message || "Delete category failed"
          );
        });
    }
  };

  useEffect(() => {
    if (update && category) {
      setValue("categoryName", category?.categoryName);
    }
  }, [update, category]);

  return (
    <div className="py-5 relative h-full ">
      <form
        className="space-y-4 "
        onSubmit={handleSubmit(addNewCategoryHandler)}
      >
        <div className="flex md:flex-row flex-col gap-4 w-full ">
          <InputField
            label="Category Name"
            required
            id="categoryName"
            type="text"
            message="This field is required*"
            placeholder="Category Name"
            register={register}
            errors={errors}
          />
        </div>

        <div className="flex  w-full justify-between items-center absolute bottom-14">
          <button
            disabled={open}
            onClick={() => setOpen(false)}
            type="button"
            className={`border border-borderColor rounded-[5px] font-metropolis  text-textColor py-[10px] px-4 text-sm font-medium`}
          >
            Cancel
          </button>
          <button
            disabled={open}
            type="submit"
            className={`font-metropolis rounded-[5px]  bg-custom-blue hover:bg-blue-800 text-white  py-[10px] px-4 text-sm font-medium`}
          >
            {open ? "Loading.." : update ? "Update" : "Save"}
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddCategoryForm;
