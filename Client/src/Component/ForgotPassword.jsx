import React, { useState } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';

const ForgotPassword = () => {
  const [email, setEmail] = useState('');

  const handleForgotPassword = async () => {
    email.trim()
    toast.loading();
    const result = await axios.post('http://localhost:8080/api/v1/user/generateResetPasswordToken', {
        email
    })
    toast.dismiss()
    if(result?.data?.success) {
        toast.success(result?.data?.result);
    }
    else {
        toast.error(result?.data?.result);
    }
  };

  return (
    <div className="bg-gray-100 min-h-screen flex items-center justify-center font-ubuntu">
      <div className="bg-white p-8 rounded shadow-md md:w-96 lg:w-2/5 xl:w-1/3">
        <h2 className="text-2xl font-bold mb-4">Forgot Password</h2>
        <div className="mb-4">
          <label htmlFor="email" className="block text-gray-600 text-sm font-medium mb-2">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-3 py-2 border rounded-md focus:outline-none focus:border-blue-500"
          />
        </div>
        <button
          onClick={handleForgotPassword}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          Submit
        </button>
      </div>
    </div>
  );
};

export default ForgotPassword;
