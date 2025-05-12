#!/usr/bin/env python3

import os
import sys
import yaml
import subprocess
import datetime
import requests

from slugify import slugify

def generate_ip_last_bits_optimized(id):
    if (id < 0):
        return None

    return id + 9


def generate_ip_last_bits(id):
    result = None
    start_value = 9
    end_value = 199

    i = -1
    for value in range(start_value, end_value):

        i += 1

        if (int(id) == 0):
            result = start_value
        if (int(id) > 0):
            for item in range(i, end_value):
                if (int(id) == i):
                    result = value
        if (int(id) < 0):
            result = None

    return result

def main():
    private_ip_address_prefix = '10.127.0.'
    for id in range(0, 300):
        last_bits_decimal = generate_ip_last_bits_optimized(id)
        #last_bits_decimal = generate_ip_last_bits_optimized(id)
        private_ip_address = private_ip_address_prefix + str(last_bits_decimal)
        print(private_ip_address)

# Execute
if __name__ == '__main__':
    main()