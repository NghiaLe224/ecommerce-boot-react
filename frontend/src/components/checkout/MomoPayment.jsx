import { Alert, AlertTitle } from '@mui/material'

const MomoPayment = () => {
  return (
     <div className='h-96 flex justify-center items-center'>
        <Alert severity="warning" variant='filled' style={{ maxWidth: "400px" }}>
            <AlertTitle>Momo Unavailable</AlertTitle>
            Momo payment is unavailable. Please use another payment method.
        </Alert>
    </div>
  )
}

export default MomoPayment
