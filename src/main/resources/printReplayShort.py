#!/usr/bin/env python
# -*- coding: utf-8 -*-
from __future__ import absolute_import, print_function, unicode_literals, division

import json
import os
import argparse

import mpyq
import sc2reader
from sc2reader import utils


def printReplay(filepath, arguments):
    """ Prints summary information about SC2 replay file """

    replay = sc2reader.load_replay(filepath, load_level=2)

    if len(replay.clients) == 1:
        return

    p0 = replay.clients[0]
    p1 = replay.clients[1]
    p1Race = p0.team.lineup
    p2Race = p1.team.lineup
    winner = replay.winner.players[0].name

    if p1Race == 'P' or p1Race == 'T':
        opponent = p1Race
    else:
        opponent = 'Z'

    if p2Race == 'P' or p2Race == 'T':
        opponent = p2Race

    archive = mpyq.MPQArchive(replay.filename)
    jsondata = archive.read_file("replay.gamemetadata.json").decode("utf-8")
    obj = json.loads(jsondata)

    print('Zv' + opponent + ' ' + winner)

    try:
        mmr0 = obj['Players'][0]['MMR']
        print(p0.name + ' ' + str(mmr0))
    except:
        pass
    try:
        mmr1 = obj['Players'][1]['MMR']
        print(p1.name + ' ' + str(mmr1))
    except:
        pass


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
                # print("\n--------------------------------------\n{0}\n".format(filepath))
                printReplay(filepath, arguments)


if __name__ == "__main__":
    main()
