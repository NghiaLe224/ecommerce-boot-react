import { FormControl, FormControlLabel, Radio, RadioGroup } from '@mui/material'
import React, { useEffect } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { addPaymentMethod, fetchPaymentMethods } from '../../store/paymentMethod-slice';

const PaymentMethod = () => {
    const dispatch = useDispatch();
    const { paymentMethod, loading: paymentLoading, error: paymentError } = useSelector((state) => state.paymentMethod);
    const { items, cartId, loading: cartLoading, error: cartError } = useSelector((state) => state.cart);

    // useEffect(() => {
    //     try {
    //         const paymentMethods = dispatch(fetchPaymentMethods());

    //     } catch (error) {
            
    //     }
    // }, [dispatch]);

    const paymentMethodHandler = (method) => {
        dispatch(addPaymentMethod(method));
    }
  return (
    <div className='max-w-md mx-auto p-5 bg-white shadow-md rounded-lg mt-16 border'>
        <h1 className='text-2xl font-semibold mb-4'>Select Payment Method</h1>
        <FormControl>
            <RadioGroup
                aria-label="payment method"
                name="paymentMethod"
                value={paymentMethod}
                onChange={(e) => paymentMethodHandler(e.target.value)}
            >
                <FormControlLabel 
                    value="VNPay" 
                    control={<Radio color='primary' />} 
                    label="VNPay" 
                    className='text-gray-700'/>

                <FormControlLabel 
                    value="Momo" 
                    control={<Radio color='primary' />} 
                    label="Momo" 
                    className='text-gray-700'/>
            </RadioGroup>
        </FormControl>
    </div>
  )
}

export default PaymentMethod;