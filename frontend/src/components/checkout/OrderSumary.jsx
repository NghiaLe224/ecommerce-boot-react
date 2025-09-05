import { formatPriceCalculation } from "../../utils/formatPrice";

const OrderSummary = ({ totalPrice, items, address, paymentMethod }) => {
  return (
    <div className="container mx-auto px-4 mb-8">
      <div className="flex flex-wrap">
        <div className="w-full lg:w-8/12 pr-4">
          <div className="space-y-4">
            <div className="p-4 border rounded-lg shadow-xs">
              <h2 className="text-2xl font-semibold mb-2">Billing Address</h2>
              <p>
                <strong>Street Address: </strong>
                {address?.streetAddress}
              </p>
              <p>
                <strong>Ward: </strong>
                {address?.ward}
              </p>
              <p>
                <strong>City: </strong>
                {address?.city}
              </p>

              <p>
                <strong>Reciever's Name: </strong>
                {address?.fullName}
              </p>
              <p>
                <strong>Phone Number: </strong>
                {address?.phoneNumber}
              </p>
            </div>
            <div className="p-4 border rounded-lg shadow-xs">
              <h2 className="text-2xl font-semibold mb-2">Payment Method</h2>
              <p>
                <strong>Method: </strong>
                {paymentMethod}
              </p>
            </div>

            <div className="pb-4 border rounded-lg shadow-xs mb-6">
              <h2 className="text-2xl font-semibold mb-2 ml-5">Order Items</h2>
              <div className="space-y-2">
                {items?.map((item) => (
                  <div key={item?.id} className="flex items-center">
                    <img
                      src={item?.imageUrl}
                      alt="Product"
                      className="w-12 h-12 rounded-sm ml-5"
                    ></img>
                    <div className="text-gray-800 ml-5">
                      <p>{item?.name}</p>
                      <p>
                        {item?.quantity} x ${item?.snapshotPrice} = $
                        {formatPriceCalculation(
                          item?.quantity,
                          item?.snapshotPrice
                        )}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        <div className="w-full lg:w-4/12 mt-4 lg:mt-0">
          <div className="border rounded-lg shadow-xs p-4 space-y-4">
            <h2 className="text-2xl font-semibold mb-2">Order Summary</h2>

            <div className="space-y-2">
              <div className="flex justify-between">
                <span>Products</span>
                <span>${formatPriceCalculation(totalPrice, 1)}</span>
              </div>
              <div className="flex justify-between">
                <span>Tax (0%)</span>
                <span>$0.00</span>
              </div>
              <div className="flex justify-between font-semibold">
                <span>SubTotal</span>
                <span>${formatPriceCalculation(totalPrice, 1)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderSummary;
