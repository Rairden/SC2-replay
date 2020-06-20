#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import absolute_import, print_function, unicode_literals, division

import os
import argparse

import sc2reader
from sc2reader import utils


def printReplay(filepath, arguments):
    """ Prints summary information about SC2 replay file """

    replay = sc2reader.load_replay(filepath, load_level=2)

    print("Date: %s" % replay.date)
    print("Map: %s" % replay.map_name)
    for player in replay.players:
        print("%s: %s" % (player.result, player))


def main():
    parser = argparse.ArgumentParser(
        description="""Prints basic information from Starcraft II replay and
        game summary files or directories."""
    )
    parser.add_argument(
        "--recursive",
        action="store_true",
        default=True,
        help="Recursively read through directories of Starcraft II files [default on]",
    )

    required = parser.add_argument_group("Required Arguments")
    required.add_argument(
        "paths",
        metavar="filename",
        type=str,
        nargs="+",
        help="Paths to one or more Starcraft II files or directories",
    )

    arguments = parser.parse_args()
    for path in arguments.paths:
        depth = -1 if arguments.recursive else 0
        for filepath in utils.get_files(path, depth=depth):
            name, ext = os.path.splitext(filepath)
            if ext.lower() == ".sc2replay":
                print(
                    "\n--------------------------------------\n{0}\n".format(filepath)
                )
                printReplay(filepath, arguments)


if __name__ == "__main__":
    main()
