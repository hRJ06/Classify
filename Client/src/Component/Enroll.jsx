import React, { useEffect, useState } from 'react';
import '../App.css';
import { redirect } from 'react-router-dom';
import toast from 'react-hot-toast';
import axios from 'axios'

const Enroll = () => {
  const [enrollmentCode, setEnrollmentCode] = useState('');
  const token = localStorage.getItem('token');
  useEffect(() => {
    if(!token) {
        toast.error("Login First")
        redirect('/login')
    }
  },[])
  const handleEnroll = async() => {
    const response = await axios.get(`http://localhost:8080/api/v1/course/enroll/${enrollmentCode}`, {
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
    if(response?.data?.success) {
      toast.success(response?.data?.result)
    }
    else {
      toast.error(response?.data?.result)
    }
  };

  return (
    <div className="container mx-auto mt-8 font-ubuntu" style={{ maxWidth: '400px' }}>
      <h2 className="text-2xl font-bold mb-4">ENROLL</h2>
      <div className="bg-white p-6 rounded shadow-md">
        <label className="block mb-4">
          Enter Code
          <input
            type="text"
            value={enrollmentCode}
            onChange={(e) => setEnrollmentCode(e.target.value)}
            className="border rounded w-full p-2"
          />
        </label>
        <button
          onClick={handleEnroll}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Enroll
        </button>
      </div>
    </div>
  );
};

export default Enroll;
