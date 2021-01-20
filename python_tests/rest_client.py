import argparse
import requests
import random

headers = {
    "Content-Type": "application/json",
    "Accept": "application/json",
}

server_rest_ports = {
    "TLV": 21000,
    "Netanya": 21001,
    "Haifa": 21002,
    "Jerusalem": 21003,
    # "Yaffo": 21004,
}


def send_request_rideOffer(use_random_port: bool, payload=None):
    payload = payload or {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',
        'endCityName': 'Netanya',
        'departureDate': '27/12/2020',
        'vacancies': '10',
        'permittedDeviation': '2'
    }


    if use_random_port:
        server_port = random.choice(list(server_rest_ports.values()))
        print(f"chose port {server_port}")
    else:
        server_port = server_rest_ports[payload["startCityName"]]

    url = "http://localhost:{}/rideOffers".format(server_port)
    response = requests.request("POST", url, headers=headers, data=str(payload))
    if response.status_code != 200:
        return None

    data = response.text
    print(data)
    return data



def send_request_getAllRideOffers():
    server_port = random.choice(server_rest_ports.values())
    url = "http://localhost:{}/rideOffers".format(server_port)
    response = requests.request("GET", url, headers=headers)
    if response.status_code != 200:
        print(response.status_code)
        return None

    data = response.text
    print(data)
    return data


def send_request_pathPlanning(use_random_port: bool, payload=None, date: str = None):
#     payload = payload or{
#      'path' : ['TLV', 'Netanya']
#      }

    date = date or '27/12/2020'
    payload = payload or ['TLV', 'Netanya']

    if use_random_port:
        server_port = random.choice(list(server_rest_ports.values()))
        print(f"chose port {server_port}")
    else:
        server_port = server_rest_ports[payload[0]]

    url = "http://localhost:{}/pathPlanning?date={}".format(server_port, date)
    print("sending : ", str(payload))
    response = requests.request("GET", url, headers=headers, json = payload)
    if response.status_code != 200:
        print('response code: ' + str(response.status_code))
        return None

    data = response.text
    print('response data: ' + data)
    return data


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('-r', '--random_port', choices=('True', 'False'),
                        help='True=send the request to a random city or False=send the request to the start city')

    parser.add_argument('-a', '--action', choices=('getAllOffers', 'rideOffer', 'pathPlanning'))

    args = parser.parse_args()
    return args


def main():
    args = parse_args()
    use_random_port = args.random_port == "True"
    if args.action == "getAllOffers":
        send_request_getAllRideOffers()

    if args.action == "rideOffer":
        send_request_rideOffer(use_random_port)

    if args.action == "pathPlanning":
        send_request_pathPlanning(use_random_port)

if __name__ == "__main__":
    main()