import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Home from './Component/Home'
import Register from './Component/Register'
import Login from './Component/Login'
import ViewCourse from './Component/ViewCourse'
import Course from './Component/Course'
import Enroll from './Component/Enroll'
import ForgotPassword from './Component/ForgotPassword'
import ResetPassword from './Component/ResetPassword'

const App = () => {
  return (
    <Routes>
      <Route path='/' element={<Home/>}></Route>
      <Route path='/register' element={<Register/>}></Route>
      <Route path='/login' element={<Login/>}></Route>
      <Route path='/getCourses' element={<ViewCourse/>}></Route>
      <Route path='/enroll' element={<Enroll/>}></Route>
      <Route path='/course/:courseId' element={<Course/>}></Route>
      <Route path='/forgot-password' element={<ForgotPassword/>}></Route>
      <Route path='/reset-password/:token' element={<ResetPassword/>}></Route>
    </Routes>
  )
}

export default App