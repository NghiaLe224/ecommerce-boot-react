import { HiOutlineTrash } from "react-icons/hi";
import SetQuantity from "./SetQuantity";
import { useDispatch, useSelector } from "react-redux";
import {
  updateQuantityServer,
  removeFromCartServer,
  setQuantityLocal,
} from "../../store/cart-slice";
import { formatPrice } from "../../utils/formatPrice";
import truncateText from "../../utils/truncateText";
import toast from "react-hot-toast"; // dùng thư viện để thông báo lỗi

const ItemContent = ({ productId, name, imageUrl, snapshotPrice }) => {
  const dispatch = useDispatch();

  const quantity = useSelector(
    (state) =>
      state.cart.items.find((item) => item.productId === productId)?.quantity
  );

  const handleChangeQuantity = async (newQuantity) => {
    if (newQuantity < 1) return;

    // 1. Optimistic update Redux ngay
    dispatch(setQuantityLocal({ id: productId, quantity: newQuantity }));

    // 2. Gọi API để sync lên server
    try {
      await dispatch(
        updateQuantityServer({ productId, quantity: newQuantity })
      ).unwrap();
    } catch (err) {
      // 3. Rollback nếu có lỗi
      dispatch(setQuantityLocal({ id: productId, quantity }));
      toast.error("Failed to update quantity. Please try again.");
    }
  };

  const handleQtyIncrease = () => {
    handleChangeQuantity(quantity + 1);
  };

  const handleQtyDecrease = () => {
    handleChangeQuantity(quantity - 1);
  };

  const removeItem = async () => {
    try {
      await dispatch(removeFromCartServer(productId)).unwrap();
      toast.success("Removed item from cart");
    } catch (err) {
      toast.error("Failed to remove item");
    }
  };

  return (
    // <div className="grid grid-cols-5 gap-4 items-center border p-4">
    //   <div className="col-span-2 flex items-center gap-4">
    //     <img
    //       src={imageUrl}
    //       className="w-16 h-16 object-cover"
    //     />
    //     <span>{truncateText(name)}</span>
    //   </div>

    //   <div>{formatPrice(snapshotPrice)}</div>

    //   <SetQuantity
    //     quantity={quantity}
    //     handeQtyIncrease={handleQtyIncrease}
    //     handleQtyDecrease={handleQtyDecrease}
    //   />

    //   <div className="flex gap-2 items-center">
    //     {formatPrice(quantity * snapshotPrice)}
    //     <button onClick={removeItem}>
    //       <HiOutlineTrash />
    //     </button>
    //   </div>
    // </div>

    <div className="grid md:grid-cols-5 grid-cols-4 md:text-md text-sm gap-4   items-center  border border-slate-200  rounded-md  lg:px-4  py-4 p-2">
      <div className="md:col-span-2 justify-self-start flex  flex-col gap-2 ">
        <div className="flex md:flex-row flex-col lg:gap-4 sm:gap-3 gap-0 items-start ">
          <h3 className="lg:text-[17px] text-sm font-semibold text-slate-600">
            {truncateText(name)}
          </h3>
        </div>

        <div className="md:w-36 sm:w-24 w-12">
          <img
            src={imageUrl}
            alt={name}
            className="md:h-36 sm:h-24 h-12 w-full object-cover rounded-md"
          />

          <div className="flex items-start gap-5 mt-3">
            <button
              onClick={removeItem}
              className="flex items-center font-semibold space-x-2 px-4 py-1 text-xs border border-rose-600 text-rose-600 rounded-md hover:bg-red-50 transition-colors duration-200"
            >
              <HiOutlineTrash size={16} className="text-rose-600" />
              Remove
            </button>
          </div>
        </div>
      </div>

      <div className="justify-self-center lg:text-[17px] text-sm text-slate-600 font-semibold">
        {formatPrice(Number(quantity * snapshotPrice))}
      </div>

      <div className="justify-self-center">
        <SetQuantity
          quantity={quantity}
          cardCounter={true}
          handeQtyIncrease={handleQtyIncrease}
          handleQtyDecrease={handleQtyDecrease}
        />
      </div>

      <div className="justify-self-center lg:text-[17px] text-sm text-slate-600 font-semibold">
        {formatPrice(Number(quantity) * Number(snapshotPrice))}
      </div>
    </div>
  );
};

export default ItemContent;
