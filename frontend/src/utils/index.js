import { FaBoxOpen, FaHome, FaShoppingCart, FaStore, FaThList } from "react-icons/fa";
import { bannerImageOne } from "./constant";

export const bannerList = [
  {
    id: 1,
    image: "https://embarkx.com/sample/placeholder.png",
    title: "Home Comfort",
    subtitle: "Living Room",
    description: "Upgrade your space with cozy and stylish sofas",
  },
  {
    id: 2,
    image: bannerImageOne,
    title: "Entertainment Hub",
    subtitle: "Smart TV",
    description: "Experience the latest in home entertainment",
  },
  {
    id: 3,
    image: "https://embarkx.com/sample/placeholder.png",
    title: "Playful Picks",
    subtitle: "Kids' Clothing",
    description: "Bright and fun styles for kids, up to 20% off",
  },
];

export const adminNavigation = [
  {
    name: "Dashboard",
    href: "/admin",
    icon: FaHome,
    current: true,
  },
  {
    name: "Orders",
    href: "/admin/orders",
    icon: FaShoppingCart,
  },
  {
    name: "Products",
    href: "/admin/products",
    icon: FaBoxOpen,
  },
  {
    name: "Categories",
    href: "/admin/categories",
    icon: FaThList,
  },
  {
    name: "Sellers",
    href: "/admin/sellers",
    icon: FaStore,
  },
];

export const sellerNavigation = [
  {
    name: "Orders",
    href: "/admin/orders",
    icon: FaShoppingCart,
    current: true,
  },
  {
    name: "Products",
    href: "/admin/products",
    icon: FaBoxOpen,
  },
];
