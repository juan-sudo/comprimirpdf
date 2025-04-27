import React, { useState, useEffect } from "react";
import axios from "axios";
import { Typography, Card, CardBody, Button, Progress } from "@material-tailwind/react";
import { ArrowDownTrayIcon } from "@heroicons/react/24/outline";
import pdfIcon from '../../layouts/img/compress-pdf-how-to.svg';

export function Home() {
  // Estados para manejar el archivo, calidad y tamaño
  const [fileSize, setFileSize] = useState(null); // Tamaño del archivo en KB, MB, GB
  const [originalPdf, setOriginalPdf] = useState(null); // URL del archivo PDF
  const [subiendo, setSubiendo] = useState(false);
  const [calidadProgress, setCalidadProgress] = useState(100); // Calidad en %
  const [tamanoProgress, setTamanoProgress] = useState(100); // Tamaño en porcentaje (para algún uso futuro)
  const [isDragging, setIsDragging] = useState(false); // Estado para controlar el efecto al arrastrar

  const [file, setFile] = useState(null); // Archivo PDF
  
  const [progressValue, setProgressValue] = useState(0); // Valor del progreso

  useEffect(() => {
    console.log("Calidad:", calidadProgress, "%");
    console.log("Tamaño:", tamanoProgress, "%");
  }, [calidadProgress, tamanoProgress]);

  useEffect(() => {
    console.log("original pfg:", originalPdf, "%");
    console.log("file size pfg:", fileSize, "%");
    
  }, [originalPdf,fileSize]);



  

  // const handleFileChange = async (e) => {
  //   // Verifica si es un cambio de archivo desde el input o un archivo arrastrado
  //   const file = e.target.files ? e.target.files[0] : (e.dataTransfer ? e.dataTransfer.files[0] : null);
    
  //   if (file) {
  //     const sizeInBytes = file.size;
  //   //  setFileSize(formatFileSize(sizeInBytes)); // Establece el tamaño del archivo formateado
  //     subirArchivo(file);
  //   } else {
  //     console.error("No file selected");
  //   }
  // };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
  };
  
  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false); // Deja de aplicar el borde de arrastre
    handleFileChange(e); // Maneja el archivo cuando se deja caer

  };

  const handleDragEnter = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(true); // Aplica el borde de arrastre
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDragging(false); // Deja de aplicar el borde de arrastre
  };


  const subirArchivo = async (file) => {
    setSubiendo(true);
    const formData = new FormData();
    formData.append("archivo", file);

    try {
      const res = await axios.post("http://localhost:8080/subir-pdf", formData);
      if (res.data.code === 200) {
       // setOriginalPdf(res.data.data);
       window.location.reload();
      } else {
        alert(res.data.message);
      }
    } catch (err) {
      console.error(err);
      alert("Error al subir");
    } finally {
      setSubiendo(false);
    }
  };

  
  // useEffect(() => {
  //   const fetchPdf = async () => {
  //     try {
  //       // Intentamos obtener el archivo de /compressed
  //       const resCompressed = await axios.get("http://localhost:8080/compressed", {
  //         responseType: "blob", // Esto asegura que el archivo PDF se reciba como un Blob (binario)
  //       });

  //       if (resCompressed.data) {
  //         // Si hay archivo en /compressed
  //         const sizeInBytes = resCompressed.data.size;
  //         const formattedSize = formatFileSize(sizeInBytes); // Supón que tienes esta función para formatear el tamaño
  //         setFileSize(formattedSize); // Establece el tamaño formateado en el estado
  //         const url = URL.createObjectURL(resCompressed.data);
  //         setOriginalPdf(url); // Asigna la URL del archivo a la variable originalPdf
  //         setIsPdfFromCompressed(true); // Marca que el PDF es de "compressed"
  //       } else {
  //         // Si no hay archivo en /compressed, obtenemos de /uploads
  //         const resUploads = await axios.get("http://localhost:8080/uploads", {
  //           responseType: "blob",
  //         });

  //         const sizeInBytes = resUploads.data.size;
  //         const formattedSize = formatFileSize(sizeInBytes);
  //         setFileSize(formattedSize);
  //         const url = URL.createObjectURL(resUploads.data);
  //         setOriginalPdf(url); // Asigna la URL del archivo de uploads
  //         setIsPdfFromCompressed(false); // Marca que el PDF es de "uploads"
  //       }
  //     } catch (err) {
  //       console.error(err);
  //       // alert("Error al obtener el archivo");
  //     }
  //   };

  //   fetchPdf(); // Llamamos a la función al cargar el componente
  // }, []);

  

  useEffect(() => {
    const fetchPdf = async () => {
      try {
        // Primero intentamos obtener el archivo de /compressed
        const resCompressed = await axios.get("http://localhost:8080/compressed", {
          responseType: "blob", // Esto asegura que el archivo PDF se reciba como un Blob (binario)
        });
  
        if (resCompressed.status === 204) {
          // Si no hay archivo en /compressed, obtenemos de /uploads
          const resUploads = await axios.get("http://localhost:8080/uploads", {
            responseType: "blob",
          });
  
          if (resUploads.status === 204) {
            // Si tampoco hay archivo en uploads
            console.log("No se encontraron archivos en 'compressed' ni en 'uploads'");
            // Aquí puedes manejarlo, por ejemplo, mostrando un mensaje
          } else {
            const sizeInBytes = resUploads.data.size;
            const formattedSize = formatFileSize(sizeInBytes);
            setFileSize(formattedSize);
       
            const url = URL.createObjectURL(resUploads.data);
            setOriginalPdf(url); // Asigna la URL del archivo de uploads
            setFile(resUploads.data); // Almacena el archivo para futuras operaciones
           // setFileFromCompressed(false); // Marca que el PDF es de "uploads"
           
          }
        } else {
          // Si hay archivo en /compressed
          const sizeInBytes = resCompressed.data.size;
          const formattedSize = formatFileSize(sizeInBytes);
          setFileSize(formattedSize);
         
          const url = URL.createObjectURL(resCompressed.data);
          setOriginalPdf(url); // Asigna la URL del archivo de compressed
          setFile(resCompressed.data); // Almacena el archivo para futuras operaciones
        //  setFileFromCompressed(true); // Marca que el PDF es de "compressed"
        }
      } catch (err) {
        console.error(err);
      }
    };
  
    fetchPdf(); // Llamamos a la función al cargar el componente
  }, []);
  

  
  const handleFileChange = async (e) => {
    // Verifica si es un cambio de archivo desde el input o un archivo arrastrado
    const file = e.target.files ? e.target.files[0] : (e.dataTransfer ? e.dataTransfer.files[0] : null);
    
    if (file) {
      const sizeInBytes = file.size;
    //  setFileSize(formatFileSize(sizeInBytes)); // Establece el tamaño del archivo formateado
      subirArchivo(file);
    } else {
      console.error("No file selected");
    }
  };
  // const handleFileChange = (event) => {
  //   setSubiendo(true);
  //   const file = event.target.files[0];
  //   if (file) {
  //     // Aquí agregarías la lógica para cargar el archivo, tal vez con un axios POST
  //     // al backend para subirlo, y luego actualizar el estado con el archivo subido.
  //     setSubiendo(false);
  //     alert("Archivo subido correctamente");
  //   }
  // };

  // useEffect(() => {
  //   const fetchPdf = async () => {
  //     try {
  //       const res = await axios.get("http://localhost:8080/uploads", {
  //         responseType: "blob", // Asegura que el archivo se reciba como un Blob
  //       });

  //       const sizeInBytes = res.data.size; // Tamaño del archivo en bytes
  //       const formattedSize = formatFileSize(sizeInBytes); // Convertirlo a un formato legible
  //       setFileSize(formattedSize); // Establecer el tamaño formateado

  //       const url = URL.createObjectURL(res.data); // Crear una URL del Blob recibido
  //       setOriginalPdf(url); // Asigna la URL del archivo al estado
  //       setFile(res.data); // Almacena el archivo para futuras operaciones
  //     } catch (err) {
  //       console.error(err);
  //     //  alert("Error al obtener el archivo");
  //     }
  //   };

  //   fetchPdf(); // Llamada a la función al cargar el componente
  // }, []);


  // useEffect(() => {
  //   // Llamada a la API para obtener el archivo PDF directamente
  //   const fetchPdf = async () => {
  //     try {
  //       const res = await axios.get("http://localhost:8080/uploads", {
  //         responseType: "blob", // Esto asegura que el archivo PDF se reciba como un Blob (binario)
  //       });

  //        // Obtener el tamaño del archivo
  //        const sizeInBytes = res.data.size; // Tamaño del archivo en bytes
  //        const formattedSize = formatFileSize(sizeInBytes); // Convertirlo a un formato legible
  //        setFileSize(formattedSize); // Establecer el tamaño formateado en el estado

  //       const url = URL.createObjectURL(res.data); // Crear una URL del Blob recibido
  //       setOriginalPdf(url); // Asigna la URL del archivo al estado originalPdf
  //     } catch (err) {
  //       console.error(err);
  //      // alert("Error al obtener el archivo");
  //     }
  //   };

  //   fetchPdf(); // Llamada a la función al cargar el componente
  // }, []);


  // Función para convertir el tamaño del archivo en bytes a KB, MB o GB
  // const formatFileSize = (sizeInBytes) => {
  //   if (sizeInBytes < 1024) return `${sizeInBytes} Bytes`;
  //   else if (sizeInBytes < 1024 * 1024) return `${(sizeInBytes / 1024).toFixed(2)} KB`;
  //   else if (sizeInBytes < 1024 * 1024 * 1024) return `${(sizeInBytes / (1024 * 1024)).toFixed(2)} MB`;
  //   else return `${(sizeInBytes / (1024 * 1024 * 1024)).toFixed(2)} GB`;
  // };

  const formatFileSize = (size) => {
    // Aquí puedes implementar la lógica para formatear el tamaño del archivo, como por ejemplo:
    if (size < 1024) return size + ' bytes';
    else if (size < 1048576) return (size / 1024).toFixed(2) + ' KB';
    else return (size / 1048576).toFixed(2) + ' MB';
  };
  const aumentarCalidad = () => {
    setCalidadProgress((prev) => (prev < 100 ? prev + 10 : 100));
  };

  const disminuirCalidad = () => {
    setCalidadProgress((prev) => (prev > 0 ? prev - 10 : 0));
  };

  const aumentarTamano = () => {
    setTamanoProgress((prev) => (prev < 100 ? prev + 10 : 100));
  };

  const disminuirTamano = () => {
    setTamanoProgress((prev) => (prev > 0 ? prev - 10 : 0));
  };

  const handleDownload = () => {
    const link = document.createElement("a");
    link.href = "/pdfs/archivo.pdf"; // Reemplaza con la URL real
    link.download = "archivo.pdf";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };


 
  const comprimirPdf = async () => {
    if (!file) return;

    setSubiendo(true); // Mostrar estado de carga

    const formData = new FormData();
    formData.append("archivo", file);
    formData.append("calidad", calidadProgress); // Calidad basada en la barra de progreso
    formData.append("tamanio", (tamanoProgress / 100).toFixed(2)); // Convertir el porcentaje de tamaño a un número entre 0 y 1

    try {

      await axios.post("http://localhost:8080/comprimir-pdf", formData, {
        onUploadProgress: (progressEvent) => {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setProgressValue(percent); // Actualizar el progreso visual
        },
      });
      const res = await axios.post("http://localhost:8080/comprimir-pdf", formData);


      if (res.data.code === 200) {
     
        setOriginalPdf(res.data.data); // Actualizar el archivo comprimido

      } else {
        alert(res.data.message);
      }
    } catch (err) {
      console.error(err);
      alert("Error al comprimir el archivo");
    } finally {
      setSubiendo(false); // Finalizar estado de carga
      window.location.reload();
    }
  };


  //LLAMAR A PDF

  
  // useEffect(() => {
  //   // Realizar la solicitud GET para obtener el archivo usando Axios
  //   axios.get("http://localhost:8080/uploads")
  //     .then((response) => {
  //       if (response.status === 200) {
  //         setOriginalPdf("/uploads"); // Asignar la ruta para mostrar el archivo
  //       } else {
  //         alert("Error al cargar el archivo");
  //       }
  //     })
  //     .catch((error) => {
  //       console.error("Error:", error);
  //       alert("Hubo un error al obtener el archivo.");
  //     });
  // }, []);

  return (

<>
    <div>
  
    {subiendo && (
  <div className="w-full mt-5">
    <Progress
      value={progressValue} // Usamos el valor dinámico del progreso
      color="indigo"
      className="h-2.5 rounded-full transition-all duration-300 ease-in-out"
    />
  {/* Muestra el valor del progreso */}
  </div>
)}


    </div>
       
          <CardBody className="w-full px-0 pt-0 pb-4 flex items-center justify-center h-full mt-3 "  >
            

            {!originalPdf ? ( 

                    <>
                    <div className="flex flex-col items-center" >
                    <div className="p-2 mt-6 text-center ">
                 <h2 className=" text-5xl font-bold mb-4">   Comprimir archivo PDF
                 </h2>
                 <p className="text-gray-600 mb-4 text-center text-lg ">
                  Comprime archivos PDF de cualquier tamaño y calidad, ajustándolos visualmente al tamaño deseado.
                </p>

                  </div>


                  <div className="pb-8 pl-4 pr-4 rounded-lg bg-gradient-to-r from-teal-500 to-cyan-500" >

                    
                      <div
              className={`flex flex-col items-center border-2 border-dashed 
                          ${isDragging ? 'border-green-500' : 'border-white'} 
                          rounded-lg py-10 px-80 mt-6 transition-all duration-300 ease-in-out`}
              onDrop={handleDrop}
              onDragOver={handleDragOver}
              onDragEnter={handleDragEnter}
              onDragLeave={handleDragLeave} 
            >
                        <img src={pdfIcon} alt="PDF" className="w-20 h-20 mb-4" />
                        <label
                          htmlFor="upload"
                          className="cursor-pointer bg-red-600 hover:bg-red-800 text-white font-semibold py-3 px-6 rounded-xl"
                        >
                          {subiendo ? "Subiendo..." : "Selecciona un archivo PDF"}
                        </label>
                        <input
                          id="upload"
                          type="file"
                          className="hidden"
                          accept="application/pdf"
                          onChange={handleFileChange}
                        />
                        <p className="text-white mt-2">o arrastra aquí tu archivo PDF</p>
                      </div>

                      </div>

                    </div>
                    </>

              


              
            ) : (

                <>

                   

<div className="mb-4 grid gap-5 xl:grid-cols-12 w-full">
  {/* Columna 1: PDF (80%) */}
  <div className="xl:col-span-8 xl:col-start-1">
    
  {!originalPdf ? (
        <div className="flex flex-col items-center border-2 border-dashed border-blue-500 rounded-lg py-10 px-20 mt-6 w-full">
          <img src={pdfIcon} alt="PDF" className="w-20 h-20 mb-4" />
          <label
            htmlFor="upload"
            className="cursor-pointer bg-red-600 hover:bg-red-800 text-white font-semibold py-3 px-6 rounded-xl"
          >
            {subiendo ? "Subiendo..." : "Selecciona un archivo PDF"}
          </label>
          <input
            id="upload"
            type="file"
            className="hidden"
            accept="application/pdf"
            onChange={handleFileChange}
          />
          <p className="text-gray-600 mt-2">o arrastra aquí tu archivo PDF</p>
        </div>
      ) : (
        <>
          <div className="flex flex-col items-center w-full relative">
            <iframe
              src={originalPdf} // La URL completa para mostrar el archivo
              width="100%" // Ajuste de ancho completo
              height="505vh" // Ajuste de altura
              className="border rounded-lg"
            ></iframe>
            <div className="flex flex-col mt-1 items-end w-full absolute bottom-5 right-5">
              <div className="bg-white p-3 rounded-xl shadow-md border border-gray-300 max-w-lg">
                <Typography variant="paragraph" className="text-gray-800 font-extrabold text-right text-xl tracking-wide">
                  {fileSize} {/* Muestra el tamaño del archivo en KB, MB o GB */}
                </Typography>
              </div>
            </div>
          </div>
        </>
      )}
    
  </div>

  {/* Columna 2: Contenido adicional (20%) */}
{fileSize && (
    <div className="xl:col-span-4 xl:col-start-9 mt-2">
      <Card className="overflow-hidden bg-gray-50">
        {/* Sección de Tamaño de archivo */}
        <CardBody className="pt-6 flex flex-col items-center gap-5 text-gray-700">
          <Typography variant="h6" className="font-bold text-2xl">
            Ajuste visual
          </Typography>
        </CardBody>

        {/* Sección de Calidad */}
        <CardBody className="flex flex-col items-center bg-white">
          <Typography variant="h6" className="text-gray-800 font-semibold text-lg">
            Calidad
          </Typography>
          <div className="w-full flex items-center gap-4">
            <Button
              onClick={disminuirCalidad}
              size="sm"
              variant="outlined"
              color="indigo"
              className="rounded-full w-9 h-9 p-0 flex items-center justify-center text-lg border-2"
            >
              −
            </Button>
            <div className="flex-1">
              <Progress
                value={calidadProgress}
                color="indigo"
                className="h-2.5 rounded-full transition-all duration-300 ease-in-out"
              />
              <Typography variant="small" className="text-center text-gray-600 mt-2">
                <span className="font-medium">{calidadProgress}%</span> completado
              </Typography>
            </div>
            <Button
              onClick={aumentarCalidad}
              size="sm"
              color="indigo"
              className="rounded-full w-9 h-9 p-0 flex items-center justify-center text-lg"
            >
              +
            </Button>
          </div>
        </CardBody>

        {/* Sección de Tamaño */}
        <CardBody className="flex flex-col items-center gap-5 bg-white">
          <Typography variant="h6" className="text-gray-800 font-semibold text-lg">
            Tamaño
          </Typography>
          <div className="w-full flex items-center gap-4">
            <Button
              onClick={disminuirTamano}
              size="sm"
              variant="outlined"
              color="indigo"
              className="rounded-full w-9 h-9 p-0 flex items-center justify-center text-lg border-2"
            >
              −
            </Button>
            <div className="flex-1">
            <Progress
              value={tamanoProgress}
              color="indigo"
              className="h-2.5 rounded-full transition-all duration-300 ease-in-out"
            />
            <Typography variant="small" className="text-center text-gray-600 mt-2">
                <span className="font-medium">{tamanoProgress}%</span> completado
              </Typography>
          </div>
            <Button
              onClick={aumentarTamano}
              size="sm"
              color="indigo"
              className="rounded-full w-9 h-9 p-0 flex items-center justify-center text-lg"
            >
              +
            </Button>
          </div>
        </CardBody>

        {/* Sección de Botón de Descarga */}
       {/* Sección de Botón de Descarga */}
             <CardBody className="flex flex-col items-center justify-center gap-5 mt-10 text-center">
                    <Button
                      onClick={comprimirPdf}
                      color="blue"
                      className="w-80 flex items-center justify-center gap-2 bg-red-600 text-white hover:bg-red-700 rounded-lg shadow-lg"
                    >
                      <ArrowDownTrayIcon className="h-8 w-8" />
                      <span className="text-xl font-semibold">Comprimir PDF</span>
                    </Button>
                     {/* Barra de progreso debajo del botón */}
                      
                  </CardBody>


      </Card>
    </div>
  )}
</div>


                
             
        </>

            )}
          </CardBody>

</>
  );
}

export default Home;
