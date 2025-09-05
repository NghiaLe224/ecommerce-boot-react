import { useEffect } from "react";
import DashboardOverview from "./DashboardOverview";
import { FaBoxOpen, FaDollarSign, FaShoppingCart } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { Loader } from "../../common/Loader";
import ErrorPage from "../../common/ErrorPage";
import toast from "react-hot-toast";
import { fetchAnalytics } from "../../../store/analytic-slice";

const Dashboard = () => {
  const dispatch = useDispatch();
  const { loading, error, analytics } = useSelector((state) => state.analytic);

  const productCount = analytics?.productCount ?? 0;
  const totalRevenue = analytics?.totalRevenue ?? 0;
  const totalOrders = analytics?.totalOrders ?? 0;

  useEffect(() => {
    // Không tự set loading; để Redux quản lý
    dispatch(fetchAnalytics())
      .unwrap()
      .then(() => toast.success("Fetch analytics successfully"))
      .catch((err) =>
        toast.error(
          typeof err === "string" ? err : err?.message || "Fetch analytics failed"
        )
      );
  }, [dispatch]);

  if (loading) return <Loader />;
  if (error) return <ErrorPage message={error} />;

  return (
    <div>
      <div
        className="flex md:flex-row mt-8 flex-col lg:justify-between 
          border border-slate-400 rounded-lg bg-gradient-to-r
          from-blue-50 to-blue-100 shadow-lg"
      >
        <DashboardOverview
          title="Total Products"
          amount={productCount}
          Icon={FaBoxOpen}
        />

        <DashboardOverview
          title="Total Orders"
          amount={totalOrders}
          Icon={FaShoppingCart}
        />

        <DashboardOverview
          title="Total Revenue"
          amount={totalRevenue}
          Icon={FaDollarSign}
          revenue
        />
      </div>
    </div>
  );
};

export default Dashboard;
