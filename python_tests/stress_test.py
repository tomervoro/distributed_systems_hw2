import random
import string

from rest_client import send_request_rideOffer, \
    send_request_getAllRideOffers, \
    send_request_pathPlanning, \
    server_rest_ports


def check_simple_case():
    offer_payload = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',
        'endCityName': 'Netanya',
        'departureDate': '27/12/2020',
        'vacancies': '4',
        'permittedDeviation': '2'
    }

    request_payload =  ["TLV", "Netanya"]
    date = '27/12/2020'


    offer_response = send_request_rideOffer(False, offer_payload)
    assert offer_response == "ride added successfully"

#     assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(False, request_payload, date)
    assert request_response


def check_random_case():
    offer_payload = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',
        'endCityName': 'Netanya',
        'departureDate': '27/12/2020',
        'vacancies': '4',
        'permittedDeviation': '2'
    }

    request_payload = {
        'path': ["TLV", "Netanya"],
        'date': '27/12/2020'
    }

    offer_response = send_request_rideOffer(True, offer_payload)
    assert offer_response == "ride added successfully"

    assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(True, request_payload)
    assert request_response


def check_path_case():
    offer_payload1 = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',
        'endCityName': 'Netanya',
        'departureDate': '27/12/2020',
        'vacancies': '1',
        'permittedDeviation': '2'
    }

    offer_payload2 = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'Netanya',
        'endCityName': 'Haifa',
        'departureDate': '27/12/2020',
        'vacancies': '1',
        'permittedDeviation': '2'
    }

    request_payload = [ "Netanya", "Haifa"]
    date = '27/12/2020'

    offer_response = send_request_rideOffer(True, offer_payload1)
#     assert offer_response == "ride added successfully"
    offer_response = send_request_rideOffer(True, offer_payload2)
#     assert offer_response == "ride added successfully"

#     assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(True, request_payload, date)
#     assert request_response

    # No available places!
    request_response = send_request_pathPlanning(True, request_payload, date)
#     assert not request_response


def check_no_vacancies_case():
    offer_payload = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',
        'endCityName': 'Netanya',
        'departureDate': '27/12/2020',
        'vacancies': '1',
        'permittedDeviation': '2'
    }

    request_payload = {
        'path': ["TLV", "Netanya"],
        'date': '27/12/2020'
    }

    offer_response = send_request_rideOffer(True, offer_payload)
    assert offer_response == "ride added successfully"

    assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(True, request_payload)
    assert request_response

    request_response = send_request_pathPlanning(True, request_payload)
    assert not request_response


def check_deviation_case():
    offer_payload = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',  # (0,0)
        'endCityName': 'Haifa',  # (1,1)
        'departureDate': '28/12/2020',
        'vacancies': '1',
        'permittedDeviation': '2'
    }


    request_payload = ["Netanya", "Haifa"]
    date = '28/12/2020'

    offer_response = send_request_rideOffer(True, offer_payload)
    assert offer_response == "ride added successfully"

#     assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(True, request_payload,date)
    assert request_response


def check_deviation_and_path_case():
    offer_payload1 = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',  # (0,0)
        'endCityName': 'Haifa',  # (1,1)
        'departureDate': '27/12/2020',
        'vacancies': '1',
        'permittedDeviation': '2'
    }
    offer_payload2 = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'Haifa',
        'endCityName': 'Netanya',
        'departureDate': '27/12/2020',
        'vacancies': '1',
        'permittedDeviation': '2'
    }


    request_payload = ["Netanya", "Haifa", "Netanya"]
    date =  '27/12/2020'

    offer_response = send_request_rideOffer(True, offer_payload1)
#     assert offer_response == "ride added successfully"
    offer_response = send_request_rideOffer(True, offer_payload2)
#     assert offer_response == "ride added successfully"

#     assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(True, request_payload, date)
    assert request_response


def check_different_dates():
    offer_payload = {
        'personName': 'Tomer',
        'phoneNumber': '05498334323',
        'startCityName': 'TLV',
        'endCityName': 'Netanya',
        'departureDate': '23/12/2020',
        'vacancies': '4',
        'permittedDeviation': '2'
    }

    request_payload = ["TLV", "Netanya"]
    date = '27/12/2020'

    offer_response = send_request_rideOffer(True, offer_payload)
    assert offer_response == "ride added successfully"

#     assert send_request_getAllRideOffers() == "TODO"

    request_response = send_request_pathPlanning(True, request_payload, date)
    assert not request_response


def stress_test():
    dates = ['11/11/1111', '03/03/0303', '66/66/6666']
    for ii in range(40):
        name = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(40))
        startCity = random.choice(list(server_rest_ports.keys()))
        cities = list(server_rest_ports.keys())
        cities.remove(startCity)
        endCity = random.choice(cities)
        offer_payload = {
            'personName': name,
            'phoneNumber': '05498334323',
            'startCityName': startCity,
            'endCityName': endCity,
            'departureDate': random.choice(dates),
            'vacancies': random.choice([str(ii) for ii in range(1,6)]),
            'permittedDeviation': random.choice([str(ii) for ii in range(1,6)])
        }

        request_payload = random.sample(server_rest_ports.keys(), random.randint(3, len(server_rest_ports.keys())))
        date = random.choice(dates)
        offer_response = send_request_rideOffer(True, offer_payload)
        assert offer_response == "ride added successfully"

#         assert send_request_getAllRideOffers() == "TODO"

        request_response = send_request_pathPlanning(True, request_payload, date)
#         assert request_response


# check_simple_case()
# check_path_case()
# check_deviation_case()
# check_deviation_and_path_case()
# check_different_dates()
stress_test()