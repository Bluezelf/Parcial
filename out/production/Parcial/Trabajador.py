import socket
import csv

HOST = "localhost"
PORT = 12345

def infoDecoder(received):
    values = received.split(",")
    edad,ingreso,prestamo,cuotas,inicio, final = int(values[0]), int(values[1]), int(values[2]), int(values[3]), int(values[4]), int(values[5])
    return edad,ingreso,prestamo,cuotas,inicio, final

def getIndex(row,limite_edad,limite_ingreso,limite_prestamo,limite_cuotas):
    id = ''
    # Edad
    if int(row[0]) <= limite_edad:
        id += '0'
    else:
        id += '1'
    
    # Sexo
    if int(row[1]) == 0:
        id += '0'
    else:
        id += '1'

    # Ingreso Mensual
    if float(row[2]) <= limite_ingreso:
        id += '0'
    else:
        id += '1'

    # Cantidad prestamo
    if float(row[3]) <= limite_prestamo:
        id += '0'
    else:
        id += '1'

    # nro cuotas
    if int(row[4]) <= limite_cuotas:
        id += '0'
    else:
        id += '1'

    valor = int(id,2)
    return valor

with open('dataset.csv', 'r') as f:
    first_line = f.readline()
    dataset = list(csv.reader(f))

ncol = first_line.count(',')

constantes = [ 0  for i in range(2 ** ncol)]

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
    sock.connect((HOST,PORT))
    running = True
    
    while running:
        returnValues = [0 for i in range(32)]
        # print("Esperando data del server")
        try:
            received = sock.recv(1024).decode('ascii')
        except:
            print("Se termino tu trabajo!")
            running = False
        else:
            # print("Trabajando...")
            edad,ingreso,prestamo,cuotas,inicio, final = infoDecoder(received)

            for i in range(inicio, final):
                index = getIndex(dataset[i],edad,ingreso,prestamo,cuotas)
                if int(dataset[i][5]) == 1:
                    returnValues[index] += 1
                else:
                    returnValues[index] -= 1
               
                    
            # print("Enviando data...")
            returnValues = ",".join(str(e) for e in returnValues) + "\n"
            sock.sendall(bytes(returnValues, encoding="utf-8"))
    
finally:
    sock.close()



