import { useLocation, Link } from "react-router-dom";
import {
  Navbar,
  Typography,
  Button,
  IconButton,
  Breadcrumbs,
  Input,
  Menu,
  MenuHandler,
  MenuList,
  MenuItem,
  Avatar,
} from "@material-tailwind/react";
import {
  UserCircleIcon,
  Cog6ToothIcon,
  BellIcon,
  ClockIcon,
  CreditCardIcon,
  Bars3Icon,
} from "@heroicons/react/24/solid";

import imgPath from "../../../src/img/iconopdf.png"; 
import {
  useMaterialTailwindController,
  setOpenConfigurator,
  setOpenSidenav,
} from "@/context";
import Swal from "sweetalert2";

import { useState, useEffect } from "react";
import axios from "axios";





export function DashboardNavbar() {
  const [controller, dispatch] = useMaterialTailwindController();
  const { fixedNavbar, openSidenav } = controller;
  const { pathname } = useLocation();
  const [layout, page] = pathname.split("/").filter((el) => el !== "");

 // Estados para manejar el archivo PDF y su tamaño
 const [fileSize, setFileSize] = useState(null); // Tamaño del archivo en KB, MB, GB
 const [originalPdf, setOriginalPdf] = useState(null); // URL del archivo PDF
 const [file, setFile] = useState(null); // Archivo PDF

 // Función para formatear el tamaño del archivo en bytes
 const formatFileSize = (size) => {
   if (size < 1024) return size + " bytes";
   else if (size < 1048576) return (size / 1024).toFixed(2) + " KB";
   else return (size / 1048576).toFixed(2) + " MB";
 };

 // Efecto para obtener el archivo PDF al cargar el componente
 useEffect(() => {
  const fetchPdf = async () => {
    try {
      const res = await axios.get("http://localhost:8080/uploads", {
        responseType: "blob", // Asegura que el archivo se reciba como un Blob
      });

      const sizeInBytes = res.data.size; // Tamaño del archivo en bytes
      const formattedSize = formatFileSize(sizeInBytes); // Convertirlo a un formato legible
      setFileSize(formattedSize); // Establecer el tamaño formateado

      const url = URL.createObjectURL(res.data); // Crear una URL del Blob recibido
      setOriginalPdf(url); // Asigna la URL del archivo al estado
      setFile(res.data); // Almacena el archivo para futuras operaciones
    } catch (err) {
      console.error(err);
      // Aquí puedes manejar errores de forma más detallada
    }
  };

  fetchPdf(); // Llamada a la función al cargar el componente
}, []);


  const handleEliminarArchivos = () => {
    Swal.fire({
      title: "¿Estás seguro?",
      text: "cargar otro archivo.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "No",
      reverseButtons: true,
      customClass: {
        confirmButton: 'bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded mx-2',  // 👈 Agregamos mx-2
        cancelButton: 'bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded mx-2',    // 👈 También aquí
      },
      buttonsStyling: false,
    }).then((result) => {
      if (result.isConfirmed) {
        // Si confirmó, hacer la llamada DELETE
        fetch("http://localhost:8080/eliminar-archivos", {
          method: "DELETE",
        })
        .then(async (response) => {
          window.location.reload();
  
          
        })
        .catch((error) => {
          console.error("Error en la petición:", error);
          Swal.fire({
            title: "Error de conexión",
            text: "No se pudo contactar con el servidor.",
            icon: "error",
            confirmButtonText: "OK",
          });
        });
      }
    });
  };

  
  return (
    <Navbar
      color={fixedNavbar ? "white" : "transparent"}
      className={`rounded-xl transition-all ${
        fixedNavbar
          ? "sticky top-4 z-40 py-3 shadow-md shadow-blue-gray-500/5"
          : "px-0 py-1"
      }`}
      fullWidth
      blurred={fixedNavbar}  style={{
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'} } 
    >
      <div className="flex flex-col-reverse justify-between gap-6 md:flex-row md:items-center" >



        
        <div className="capitalize">

          
        
        
          <img src={imgPath} alt="Descripción de la imagen" className="w-[8rem] h-auto" />
          
        </div>

        
        <div className="flex items-center gap-8">
        {originalPdf ? (
        <button 
        type="button" 
        onClick={handleEliminarArchivos}
        className="text-white bg-red-500 hover:bg-red-700 border border-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 font-medium rounded-lg text-sm px-5 py-2.5 text-center inline-flex items-center dark:focus:ring-gray-600 dark:bg-gray-800 dark:border-gray-700 dark:text-white dark:hover:bg-gray-700 me-2 mb-2">
        <svg
    xmlns="http://www.w3.org/2000/svg"
    className="w-6 h-6 me-2"
    fill="none"
    viewBox="0 0 24 24"
    stroke="currentColor"
    strokeWidth="2"
  >
    <path
      strokeLinecap="round"
      strokeLinejoin="round"
      d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2m-4-4l-4-4m0 0l-4 4m4-4v12"
    />
  </svg>
       
        Subir otro archivo
        </button>
          ) : (
            <p>No se ha cargado ningún archivo PDF.</p>
          )}
            


                     {/* Barra de progreso debajo del botón */}
                      
                  
          
          <IconButton
            variant="text"
            color="blue-gray"
            className="grid xl:hidden"
            onClick={() => setOpenSidenav(dispatch, !openSidenav)}
          >
            <Bars3Icon strokeWidth={3} className="h-6 w-6 text-blue-gray-500" />
          </IconButton>
          {/* <Link to="/auth/sign-in">
            <Button
              variant="text"
              color="blue-gray"
              className="hidden items-center gap-1 px-4 xl:flex normal-case"
            >
              <UserCircleIcon className="h-5 w-5 text-blue-gray-500" />
              Sign In 
            </Button>
            <IconButton
              variant="text"
              color="blue-gray"
              className="grid xl:hidden"
            >
              <UserCircleIcon className="h-5 w-5 text-blue-gray-500" />
            </IconButton>
          </Link>
           */}
        
        </div>
      </div>
    </Navbar>
  );
}

DashboardNavbar.displayName = "/src/widgets/layout/dashboard-navbar.jsx";

export default DashboardNavbar;
