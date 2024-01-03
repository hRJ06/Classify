import React, { useState } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';
import { AiOutlineEye, AiOutlineEyeInvisible } from 'react-icons/ai';
import { useNavigate, useParams } from 'react-router-dom';

const ResetPassword = () => {
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const {token} = useParams();
    const navigate = useNavigate();

    const handleResetPassword = async () => {
      if(password !== confirmPassword) {
        toast.error("Password Don't Match");
        return;
      }
      const response = await axios.post(`http://localhost:8080/api/v1/user/resetPassword/${token}`, {
        password, confirmPassword
      })
      if(response?.data?.success) {
        toast.success(response?.data?.result)
        navigate('/login');
      }
      else {
        toast.error(response?.data?.result)
      }
    };
  
    const handleTogglePassword = () => {
      setShowPassword(!showPassword);
    };
  
    const handleToggleConfirmPassword = () => {
      setShowConfirmPassword(!showConfirmPassword);
    };
  
    return (
      <div className="bg-gray-100 min-h-screen flex items-center justify-center font-ubuntu">
        <div className="bg-white p-8 rounded shadow-md md:w-96 lg:w-2/5 xl:w-1/3">
          <h2 className="text-2xl font-bold mb-4">Reset Password</h2>
          <div className="mb-4 relative">
            <label htmlFor="password" className="block text-gray-600 text-sm font-medium mb-2">New Password</label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-3 py-2 border rounded-md focus:outline-none focus:border-blue-500"
              />
              <span
                className="absolute top-3 right-3 cursor-pointer"
                onClick={handleTogglePassword}
              >
                {showPassword ? <AiOutlineEye /> : <AiOutlineEyeInvisible />}
              </span>
            </div>
          </div>
          <div className="mb-4 relative">
            <label htmlFor="confirmPassword" className="block text-gray-600 text-sm font-medium mb-2">Confirm Password</label>
            <div className="relative">
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full px-3 py-2 border rounded-md focus:outline-none focus:border-blue-500"
              />
              <span
                className="absolute top-3 right-3 cursor-pointer"
                onClick={handleToggleConfirmPassword}
              >
                {showConfirmPassword ? <AiOutlineEye /> : <AiOutlineEyeInvisible />}
              </span>
            </div>
          </div>
          <button
            onClick={handleResetPassword}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            Submit
          </button>
        </div>
      </div>
    );
  };
  
  export default ResetPassword;
  