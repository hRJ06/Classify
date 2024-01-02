import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Home from './Component/Home'
import Register from './Component/Register'
import Login from './Component/Login'
import ViewCourse from './Component/ViewCourse'
import Course from './Component/Course'
import Enroll from './Component/Enroll'

const App = () => {
  return (
    <Routes>
      <Route path='/' element={<Home/>}></Route>
      <Route path='/register' element={<Register/>}></Route>
      <Route path='/login' element={<Login/>}></Route>
      <Route path='/getCourses' element={<ViewCourse/>}></Route>
      <Route path='/enroll' element={<Enroll/>}></Route>
      <Route path='/course/:courseId' element={<Course/>}></Route>
    </Routes>
  )
}

export default App