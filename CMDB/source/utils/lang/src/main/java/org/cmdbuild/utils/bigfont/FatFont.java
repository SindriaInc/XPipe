/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.bigfont;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ataboga
 */
public class FatFont {

    protected static final Map<Character, String> NUMBERS = map(
            '0', """

                      000000000
                    00:::::::::00
                  00:::::::::::::00
                 0:::::::000:::::::0
                 0::::::0   0::::::0
                 0:::::0     0:::::0
                 0:::::0     0:::::0
                 0:::::0 000 0:::::0
                 0:::::0 000 0:::::0
                 0:::::0     0:::::0
                 0:::::0     0:::::0
                 0::::::0   0::::::0
                 0:::::::000:::::::0
                  00:::::::::::::00
                    00:::::::::00
                      000000000""",
            '1', """

                   1111111
                  1::::::1
                 1:::::::1
                 111:::::1
                    1::::1
                    1::::1
                    1::::1
                    1::::l
                    1::::l
                    1::::l
                    1::::l
                    1::::l
                 111::::::111
                 1::::::::::1
                 1::::::::::1
                 111111111111""",
            '2', """

                  222222222222222
                 2:::::::::::::::22
                 2::::::222222:::::2
                 2222222     2:::::2
                             2:::::2
                             2:::::2
                          2222::::2
                     22222::::::22
                   22::::::::222
                  2:::::22222
                 2:::::2
                 2:::::2
                 2:::::2       222222
                 2::::::2222222:::::2
                 2::::::::::::::::::2
                 22222222222222222222""",
            '3', """

                  333333333333333
                 3:::::::::::::::33
                 3::::::33333::::::3
                 3333333     3:::::3
                             3:::::3
                             3:::::3
                     33333333:::::3
                     3:::::::::::3
                     33333333:::::3
                             3:::::3
                             3:::::3
                             3:::::3
                 3333333     3:::::3
                 3::::::33333::::::3
                 3:::::::::::::::33
                  333333333333333""",
            '4', """

                        444444444
                       4::::::::4
                      4:::::::::4
                     4::::44::::4
                    4::::4 4::::4
                   4::::4  4::::4
                  4::::4   4::::4
                 4::::444444::::444
                 4::::::::::::::::4
                 4444444444:::::444
                           4::::4
                           4::::4
                           4::::4
                         44::::::44
                         4::::::::4
                         4444444444""",
            '5', """

                 555555555555555555
                 5::::::::::::::::5
                 5::::::::::::::::5
                 5:::::555555555555
                 5:::::5
                 5:::::5
                 5:::::5555555555
                 5:::::::::::::::5
                 555555555555:::::5
                             5:::::5
                             5:::::5
                 5555555     5:::::5
                 5::::::55555::::::5
                  55:::::::::::::55
                    55:::::::::55
                      555555555""",
            '6', """

                         66666666
                        6::::::6
                       6::::::6
                      6::::::6
                     6::::::6
                    6::::::6
                   6::::::6
                  6::::::::66666
                 6::::::::::::::66
                 6::::::66666:::::6
                 6:::::6     6:::::6
                 6:::::6     6:::::6
                 6::::::66666::::::6
                  66:::::::::::::66
                    66:::::::::66
                      666666666""",
            '7', """

                 77777777777777777777
                 7::::::::::::::::::7
                 7::::::::::::::::::7
                 777777777777:::::::7
                            7::::::7
                           7::::::7
                          7::::::7
                         7::::::7
                        7::::::7
                       7::::::7
                      7::::::7
                     7::::::7
                    7::::::7
                   7::::::7
                  7::::::7
                 77777777""",
            '8', """

                      888888888
                    88:::::::::88
                  88:::::::::::::88
                 8::::::88888::::::8
                 8:::::8     8:::::8
                 8:::::8     8:::::8
                  8:::::88888:::::8
                   8:::::::::::::8
                  8:::::88888:::::8
                 8:::::8     8:::::8
                 8:::::8     8:::::8
                 8:::::8     8:::::8
                 8::::::88888::::::8
                  88:::::::::::::88
                    88:::::::::88
                      888888888""",
            '9', """

                      999999999
                    99:::::::::99
                  99:::::::::::::99
                 9::::::99999::::::9
                 9:::::9     9:::::9
                 9:::::9     9:::::9
                  9:::::99999::::::9
                   99::::::::::::::9
                     99999::::::::9
                          9::::::9
                         9::::::9
                        9::::::9
                       9::::::9
                      9::::::9
                     9::::::9
                    99999999""");

    protected static final Map<Character, String> UPPERCASE_LETTERS = map(
            'A', """

                                AAA
                               A:::A
                              A:::::A
                             A:::::::A
                            A:::::::::A
                           A:::::A:::::A
                          A:::::A A:::::A
                         A:::::A   A:::::A
                        A:::::A     A:::::A
                       A:::::AAAAAAAAA:::::A
                      A:::::::::::::::::::::A
                     A:::::AAAAAAAAAAAAA:::::A
                    A:::::A             A:::::A
                   A:::::A               A:::::A
                  A:::::A                 A:::::A
                 AAAAAAA                   AAAAAAA""",
            'B', """

                 BBBBBBBBBBBBBBBBB
                 B::::::::::::::::B
                 B::::::BBBBBB:::::B
                 BB:::::B     B:::::B
                   B::::B     B:::::B
                   B::::B     B:::::B
                   B::::BBBBBB:::::B
                   B:::::::::::::BB
                   B::::BBBBBB:::::B
                   B::::B     B:::::B
                   B::::B     B:::::B
                   B::::B     B:::::B
                 BB:::::BBBBBB::::::B
                 B:::::::::::::::::B
                 B::::::::::::::::B
                 BBBBBBBBBBBBBBBBB""",
            'C', """

                         CCCCCCCCCCCCC
                      CCC::::::::::::C
                    CC:::::::::::::::C
                   C:::::CCCCCCCC::::C
                  C:::::C       CCCCCC
                 C:::::C
                 C:::::C
                 C:::::C
                 C:::::C
                 C:::::C
                 C:::::C
                  C:::::C       CCCCCC
                   C:::::CCCCCCCC::::C
                    CC:::::::::::::::C
                      CCC::::::::::::C
                         CCCCCCCCCCCCC""",
            'D', """

                 DDDDDDDDDDDDD
                 D::::::::::::DDD
                 D:::::::::::::::DD
                 DDD:::::DDDDD:::::D
                   D:::::D    D:::::D
                   D:::::D     D:::::D
                   D:::::D     D:::::D
                   D:::::D     D:::::D
                   D:::::D     D:::::D
                   D:::::D     D:::::D
                   D:::::D     D:::::D
                   D:::::D    D:::::D
                 DDD:::::DDDDD:::::D
                 D:::::::::::::::DD
                 D::::::::::::DDD
                 DDDDDDDDDDDDD""",
            'E', """

                 EEEEEEEEEEEEEEEEEEEEEE
                 E::::::::::::::::::::E
                 E::::::::::::::::::::E
                 EE::::::EEEEEEEEE::::E
                   E:::::E       EEEEEE
                   E:::::E
                   E::::::EEEEEEEEEE
                   E:::::::::::::::E
                   E:::::::::::::::E
                   E::::::EEEEEEEEEE
                   E:::::E
                   E:::::E       EEEEEE
                 EE::::::EEEEEEEE:::::E
                 E::::::::::::::::::::E
                 E::::::::::::::::::::E
                 EEEEEEEEEEEEEEEEEEEEEE""",
            'F', """

                 FFFFFFFFFFFFFFFFFFFFFF
                 F::::::::::::::::::::F
                 F::::::::::::::::::::F
                 FF::::::FFFFFFFFF::::F
                   F:::::F       FFFFFF
                   F:::::F
                   F::::::FFFFFFFFFF
                   F:::::::::::::::F
                   F:::::::::::::::F
                   F::::::FFFFFFFFFF
                   F:::::F
                   F:::::F
                 FF:::::::FF
                 F::::::::FF
                 F::::::::FF
                 FFFFFFFFFFF""",
            'G', """

                         GGGGGGGGGGGGG
                      GGG::::::::::::G
                    GG:::::::::::::::G
                   G:::::GGGGGGGG::::G
                  G:::::G       GGGGGG
                 G:::::G
                 G:::::G
                 G:::::G    GGGGGGGGGG
                 G:::::G    G::::::::G
                 G:::::G    GGGGG::::G
                 G:::::G        G::::G
                  G:::::G       G::::G
                   G:::::GGGGGGGG::::G
                    GG:::::::::::::::G
                      GGG::::::GGG:::G
                         GGGGGG   GGGG""",
            'H', """

                 HHHHHHHHH     HHHHHHHHH
                 H:::::::H     H:::::::H
                 H:::::::H     H:::::::H
                 HH::::::H     H::::::HH
                   H:::::H     H:::::H
                   H:::::H     H:::::H
                   H::::::HHHHH::::::H
                   H:::::::::::::::::H
                   H:::::::::::::::::H
                   H::::::HHHHH::::::H
                   H:::::H     H:::::H
                   H:::::H     H:::::H
                 HH::::::H     H::::::HH
                 H:::::::H     H:::::::H
                 H:::::::H     H:::::::H
                 HHHHHHHHH     HHHHHHHHH""",
            'I', """

                 IIIIIIIIII
                 I::::::::I
                 I::::::::I
                 II::::::II
                   I::::I
                   I::::I
                   I::::I
                   I::::I
                   I::::I
                   I::::I
                   I::::I
                   I::::I
                 II::::::II
                 I::::::::I
                 I::::::::I
                 IIIIIIIIII""",
            'J', """

                           JJJJJJJJJJJ
                           J:::::::::J
                           J:::::::::J
                           JJ:::::::JJ
                             J:::::J
                             J:::::J
                             J:::::J
                             J:::::j
                             J:::::J
                 JJJJJJJ     J:::::J
                 J:::::J     J:::::J
                 J::::::J   J::::::J
                 J:::::::JJJ:::::::J
                  JJ:::::::::::::JJ
                    JJ:::::::::JJ
                      JJJJJJJJJ""",
            'K', """

                 KKKKKKKKK    KKKKKKK
                 K:::::::K    K:::::K
                 K:::::::K    K:::::K
                 K:::::::K   K::::::K
                 KK::::::K  K:::::KKK
                   K:::::K K:::::K
                   K::::::K:::::K
                   K:::::::::::K
                   K:::::::::::K
                   K::::::K:::::K
                   K:::::K K:::::K
                 KK::::::K  K:::::KKK
                 K:::::::K   K::::::K
                 K:::::::K    K:::::K
                 K:::::::K    K:::::K
                 KKKKKKKKK    KKKKKKK""",
            'L', """

                 LLLLLLLLLLL
                 L:::::::::L
                 L:::::::::L
                 LL:::::::LL
                   L:::::L
                   L:::::L
                   L:::::L
                   L:::::L
                   L:::::L
                   L:::::L
                   L:::::L
                   L:::::L         LLLLLL
                 LL:::::::LLLLLLLLL:::::L
                 L::::::::::::::::::::::L
                 L::::::::::::::::::::::L
                 LLLLLLLLLLLLLLLLLLLLLLLL""",
            'M', """

                 MMMMMMMM               MMMMMMMM
                 M:::::::M             M:::::::M
                 M::::::::M           M::::::::M
                 M:::::::::M         M:::::::::M
                 M::::::::::M       M::::::::::M
                 M:::::::::::M     M:::::::::::M
                 M:::::::M::::M   M::::M:::::::M
                 M::::::M M::::M M::::M M::::::M
                 M::::::M  M::::M::::M  M::::::M
                 M::::::M   M:::::::M   M::::::M
                 M::::::M    M:::::M    M::::::M
                 M::::::M     MMMMM     M::::::M
                 M::::::M               M::::::M
                 M::::::M               M::::::M
                 M::::::M               M::::::M
                 MMMMMMMM               MMMMMMMM""",
            'N', """

                 NNNNNNNN        NNNNNNNN
                 N:::::::N       N::::::N
                 N::::::::N      N::::::N
                 N:::::::::N     N::::::N
                 N::::::::::N    N::::::N
                 N:::::::::::N   N::::::N
                 N:::::::N::::N  N::::::N
                 N::::::N N::::N N::::::N
                 N::::::N  N::::N:::::::N
                 N::::::N   N:::::::::::N
                 N::::::N    N::::::::::N
                 N::::::N     N:::::::::N
                 N::::::N      N::::::::N
                 N::::::N       N:::::::N
                 N::::::N        N::::::N
                 NNNNNNNN         NNNNNNN""",
            'O', """

                      OOOOOOOOO
                    OO:::::::::OO
                  OO:::::::::::::OO
                 O:::::::OOO:::::::O
                 O::::::O   O::::::O
                 O:::::O     O:::::O
                 O:::::O     O:::::O
                 O:::::O     O:::::O
                 O:::::O     O:::::O
                 O:::::O     O:::::O
                 O:::::O     O:::::O
                 O::::::O   O::::::O
                 O:::::::OOO:::::::O
                  OO:::::::::::::OO
                    OO:::::::::OO
                      OOOOOOOOO""",
            'P', """

                 PPPPPPPPPPPPPPPPP
                 P::::::::::::::::P
                 P::::::PPPPPP:::::P
                 PP:::::P     P:::::P
                   P::::P     P:::::P
                   P::::P     P:::::P
                   P::::PPPPPP:::::P
                   P:::::::::::::PP
                   P::::PPPPPPPPP
                   P::::P
                   P::::P
                   P::::P
                 PP::::::PP
                 P::::::::P
                 P::::::::P
                 PPPPPPPPPP""",
            'Q', """

                      QQQQQQQQQ
                    QQ:::::::::QQ
                  QQ:::::::::::::QQ
                 Q:::::::QQQ:::::::Q
                 Q::::::O   Q::::::Q
                 Q:::::O     Q:::::Q
                 Q:::::O     Q:::::Q
                 Q:::::O     Q:::::Q
                 Q:::::O     Q:::::Q
                 Q:::::O     Q:::::Q
                 Q:::::O  QQQQ:::::Q
                 Q::::::O Q::::::::Q
                 Q:::::::QQ::::::::Q
                  QQ::::::::::::::Q
                    QQ:::::::::::Q
                      QQQQQQQQ::::QQ
                              Q:::::Q
                               QQQQQQ""",
            'R', """

                 RRRRRRRRRRRRRRRRR
                 R::::::::::::::::R
                 R::::::RRRRRR:::::R
                 RR:::::R     R:::::R
                   R::::R     R:::::R
                   R::::R     R:::::R
                   R::::RRRRRR:::::R
                   R:::::::::::::RR
                   R::::RRRRRR:::::R
                   R::::R     R:::::R
                   R::::R     R:::::R
                   R::::R     R:::::R
                 RR:::::R     R:::::R
                 R::::::R     R:::::R
                 R::::::R     R:::::R
                 RRRRRRRR     RRRRRRR""",
            'S', """

                    SSSSSSSSSSSSSSS
                  SS:::::::::::::::S
                 S:::::SSSSSS::::::S
                 S:::::S     SSSSSSS
                 S:::::S
                 S:::::S
                  S::::SSSS
                   SS::::::SSSSS
                     SSS::::::::SS
                        SSSSSS::::S
                             S:::::S
                             S:::::S
                 SSSSSSS     S:::::S
                 S::::::SSSSSS:::::S
                 S:::::::::::::::SS
                  SSSSSSSSSSSSSSS""",
            'T', """

                 TTTTTTTTTTTTTTTTTTTTTTT
                 T:::::::::::::::::::::T
                 T:::::::::::::::::::::T
                 T:::::TT:::::::TT:::::T
                 TTTTTT  T:::::T  TTTTTT
                         T:::::T
                         T:::::T
                         T:::::T
                         T:::::T
                         T:::::T
                         T:::::T
                         T:::::T
                       TT:::::::TT
                       T:::::::::T
                       T:::::::::T
                       TTTTTTTTTTT""",
            'U', """

                 UUUUUUUU     UUUUUUUU
                 U::::::U     U::::::U
                 U::::::U     U::::::U
                 UU:::::U     U:::::UU
                  U:::::U     U:::::U
                  U:::::D     D:::::U
                  U:::::D     D:::::U
                  U:::::D     D:::::U
                  U:::::D     D:::::U
                  U:::::D     D:::::U
                  U:::::D     D:::::U
                  U::::::U   U::::::U
                  U:::::::UUU:::::::U
                   UU:::::::::::::UU
                     UU:::::::::UU
                       UUUUUUUUU""",
            'V', """

                 VVVVVVVV           VVVVVVVV
                 V::::::V           V::::::V
                 V::::::V           V::::::V
                 V::::::V           V::::::V
                  V:::::V           V:::::V
                   V:::::V         V:::::V
                    V:::::V       V:::::V
                     V:::::V     V:::::V
                      V:::::V   V:::::V
                       V:::::V V:::::V
                        V:::::V:::::V
                         V:::::::::V
                          V:::::::V
                           V:::::V
                            V:::V
                             VVV""",
            'W', """

                 WWWWWWWW                           WWWWWWWW
                 W::::::W                           W::::::W
                 W::::::W                           W::::::W
                 W::::::W                           W::::::W
                  W:::::W           WWWWW           W:::::W
                   W:::::W         W:::::W         W:::::W
                    W:::::W       W:::::::W       W:::::W
                     W:::::W     W:::::::::W     W:::::W
                      W:::::W   W:::::W:::::W   W:::::W
                       W:::::W W:::::W W:::::W W:::::W
                        W:::::W:::::W   W:::::W:::::W
                         W:::::::::W     W:::::::::W
                          W:::::::W       W:::::::W
                           W:::::W         W:::::W
                            W:::W           W:::W
                             WWW             WWW""",
            'X', """

                 XXXXXXX       XXXXXXX
                 X:::::X       X:::::X
                 X:::::X       X:::::X
                 X::::::X     X::::::X
                 XXX:::::X   X:::::XXX
                    X:::::X X:::::X
                     X:::::X:::::X
                      X:::::::::X
                      X:::::::::X
                     X:::::X:::::X
                    X:::::X X:::::X
                 XXX:::::X   X:::::XXX
                 X::::::X     X::::::X
                 X:::::X       X:::::X
                 X:::::X       X:::::X
                 XXXXXXX       XXXXXXX""",
            'Y', """

                 YYYYYYY       YYYYYYY
                 Y:::::Y       Y:::::Y
                 Y:::::Y       Y:::::Y
                 Y::::::Y     Y::::::Y
                 YYY:::::Y   Y:::::YYY
                    Y:::::Y Y:::::Y
                     Y:::::Y:::::Y
                      Y:::::::::Y
                       Y:::::::Y
                        Y:::::Y
                        Y:::::Y
                        Y:::::Y
                        Y:::::Y
                     YYYY:::::YYYY
                     Y:::::::::::Y
                     YYYYYYYYYYYYY""",
            'Z', """

                 ZZZZZZZZZZZZZZZZZZZ
                 Z:::::::::::::::::Z
                 Z:::::::::::::::::Z
                 Z:::ZZZZZZZZ:::::Z
                 ZZZZZ     Z:::::Z
                         Z:::::Z
                        Z:::::Z
                       Z:::::Z
                      Z:::::Z
                     Z:::::Z
                    Z:::::Z
                 ZZZ:::::Z     ZZZZZ
                 Z::::::ZZZZZZZZ:::Z
                 Z:::::::::::::::::Z
                 Z:::::::::::::::::Z
                 ZZZZZZZZZZZZZZZZZZZ""");

    protected static final Map<Character, String> LOWERCASE_LETTERS = map(
            'a', """





                   aaaaaaaaaaaaa
                   a::::::::::::a
                   aaaaaaaaa:::::a
                            a::::a
                     aaaaaaa:::::a
                   aa::::::::::::a
                  a::::aaaa::::::a
                 a::::a    a:::::a
                 a::::a    a:::::a
                 a:::::aaaa::::::a
                  a::::::::::aa:::a
                   aaaaaaaaaa  aaaa""",
            'b', """
                 bbbbbbbb
                 b::::::b
                 b::::::b
                 b::::::b
                  b:::::b
                  b:::::bbbbbbbbb
                  b::::::::::::::bb
                  b::::::::::::::::b
                  b:::::bbbbb:::::::b
                  b:::::b    b::::::b
                  b:::::b     b:::::b
                  b:::::b     b:::::b
                  b:::::b     b:::::b
                  b:::::bbbbbb::::::b
                  b::::::::::::::::b
                  b:::::::::::::::b
                  bbbbbbbbbbbbbbbb""",
            'c', """





                     cccccccccccccccc
                   cc:::::::::::::::c
                  c:::::::::::::::::c
                 c:::::::cccccc:::::c
                 c::::::c     ccccccc
                 c:::::c
                 c:::::c
                 c::::::c     ccccccc
                 c:::::::cccccc:::::c
                  c:::::::::::::::::c
                   cc:::::::::::::::c
                     cccccccccccccccc""",
            'd', """
                             dddddddd
                             d::::::d
                             d::::::d
                             d::::::d
                             d:::::d
                     ddddddddd:::::d
                   dd::::::::::::::d
                  d::::::::::::::::d
                 d:::::::ddddd:::::d
                 d::::::d    d:::::d
                 d:::::d     d:::::d
                 d:::::d     d:::::d
                 d:::::d     d:::::d
                 d::::::ddddd::::::dd
                  d:::::::::::::::::d
                   d:::::::::ddd::::d
                    ddddddddd   ddddd""",
            'e', """





                     eeeeeeeeeeee
                   ee::::::::::::ee
                  e::::::eeeee:::::ee
                 e::::::e     e:::::e
                 e:::::::eeeee::::::e
                 e:::::::::::::::::e
                 e::::::eeeeeeeeeee
                 e:::::::e
                 e::::::::e
                  e::::::::eeeeeeee
                   ee:::::::::::::e
                     eeeeeeeeeeeeee""",
            'f', """

                     ffffffffffffffff
                    f::::::::::::::::f
                   f::::::::::::::::::f
                   f::::::fffffff:::::f
                   f:::::f       ffffff
                   f:::::f
                  f:::::::ffffff
                  f::::::::::::f
                  f::::::::::::f
                  f:::::::ffffff
                   f:::::f
                   f:::::f
                  f:::::::f
                  f:::::::f
                  f:::::::f
                  fffffffff""",
            'g', """





                    ggggggggg   ggggg
                   g:::::::::ggg::::g
                  g:::::::::::::::::g
                 g::::::ggggg::::::gg
                 g:::::g     g:::::g
                 g:::::g     g:::::g
                 g:::::g     g:::::g
                 g::::::g    g:::::g
                 g:::::::ggggg:::::g
                  g::::::::::::::::g
                   gg::::::::::::::g
                     gggggggg::::::g
                             g:::::g
                 gggggg      g:::::g
                 g:::::gg   gg:::::g
                  g::::::ggg:::::::g
                   gg:::::::::::::g
                     ggg::::::ggg
                        gggggg""",
            'h', """

                 hhhhhhh
                 h:::::h
                 h:::::h
                 h:::::h
                  h::::h hhhhh
                  h::::hh:::::hhh
                  h::::::::::::::hh
                  h:::::::hhh::::::h
                  h::::::h   h::::::h
                  h:::::h     h:::::h
                  h:::::h     h:::::h
                  h:::::h     h:::::h
                  h:::::h     h:::::h
                  h:::::h     h:::::h
                  h:::::h     h:::::h
                  hhhhhhh     hhhhhhh""",
            'i', """

                   iiii
                  i::::i
                   iiii

                 iiiiiii
                 i:::::i
                  i::::i
                  i::::i
                  i::::i
                  i::::i
                  i::::i
                  i::::i
                 i::::::i
                 i::::::i
                 i::::::i
                 iiiiiiii""",
            'j', """

                              jjjj
                             j::::j
                              jjjj

                            jjjjjjj
                            j:::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                             j::::j
                   jjjj      j::::j
                  j::::jj   j:::::j
                  j::::::jjj::::::j
                   jj::::::::::::j
                     jjj::::::jjj
                        jjjjjj""",
            'k', """

                 kkkkkkkk
                 k::::::k
                 k::::::k
                 k::::::k
                  k:::::k    kkkkkkk
                  k:::::k   k:::::k
                  k:::::k  k:::::k
                  k:::::k k:::::k
                  k::::::k:::::k
                  k:::::::::::k
                  k:::::::::::k
                  k::::::k:::::k
                 k::::::k k:::::k
                 k::::::k  k:::::k
                 k::::::k   k:::::k
                 kkkkkkkk    kkkkkkk""",
            'l', """

                 lllllll
                 l:::::l
                 l:::::l
                 l:::::l
                  l::::l
                  l::::l
                  l::::l
                  l::::l
                  l::::l
                  l::::l
                  l::::l
                  l::::l
                 l::::::l
                 l::::::l
                 l::::::l
                 llllllll""",
            'm', """





                    mmmmmmm    mmmmmmm
                  mm:::::::m  m:::::::mm
                 m::::::::::mm::::::::::m
                 m::::::::::::::::::::::m
                 m:::::mmm::::::mmm:::::m
                 m::::m   m::::m   m::::m
                 m::::m   m::::m   m::::m
                 m::::m   m::::m   m::::m
                 m::::m   m::::m   m::::m
                 m::::m   m::::m   m::::m
                 m::::m   m::::m   m::::m
                 mmmmmm   mmmmmm   mmmmmm""",
            'n', """





                 nnnn  nnnnnnnn
                 n:::nn::::::::nn
                 n::::::::::::::nn
                 nn:::::::::::::::n
                   n:::::nnnn:::::n
                   n::::n    n::::n
                   n::::n    n::::n
                   n::::n    n::::n
                   n::::n    n::::n
                   n::::n    n::::n
                   n::::n    n::::n
                   nnnnnn    nnnnnn""",
            'o', """





                    ooooooooooo
                  oo:::::::::::oo
                 o:::::::::::::::o
                 o:::::ooooo:::::o
                 o::::o     o::::o
                 o::::o     o::::o
                 o::::o     o::::o
                 o::::o     o::::o
                 o:::::ooooo:::::o
                 o:::::::::::::::o
                  oo:::::::::::oo
                    ooooooooooo""",
            'p', """





                 ppppp   ppppppppp
                 p::::ppp:::::::::p
                 p:::::::::::::::::p
                 pp::::::ppppp::::::p
                  p:::::p     p:::::p
                  p:::::p     p:::::p
                  p:::::p     p:::::p
                  p:::::p    p::::::p
                  p:::::ppppp:::::::p
                  p::::::::::::::::p
                  p::::::::::::::pp
                  p::::::pppppppp
                  p:::::p
                  p:::::p
                 p:::::::p
                 p:::::::p
                 p:::::::p
                 ppppppppp""",
            'q', """





                    qqqqqqqqq   qqqqq
                   q:::::::::qqq::::q
                  q:::::::::::::::::q
                 q::::::qqqqq::::::qq
                 q:::::q     q:::::q
                 q:::::q     q:::::q
                 q:::::q     q:::::q
                 q::::::q    q:::::q
                 q:::::::qqqqq:::::q
                  q::::::::::::::::q
                   qq::::::::::::::q
                     qqqqqqqq::::::q
                             q:::::q
                             q:::::q
                            q:::::::q
                            q:::::::q
                            q:::::::q
                            qqqqqqqqq""",
            'r', """





                 rrrrr   rrrrrrrrr
                 r::::rrr:::::::::r
                 r:::::::::::::::::r
                 rr::::::rrrrr::::::r
                  r:::::r     r:::::r
                  r:::::r     rrrrrrr
                  r:::::r
                  r:::::r
                  r:::::r
                  r:::::r
                  r:::::r
                  rrrrrrr""",
            's', """





                     ssssssssss
                   ss::::::::::s
                 ss:::::::::::::s
                 s::::::ssss:::::s
                  s:::::s  ssssss
                    s::::::s
                       s::::::s
                 ssssss   s:::::s
                 s:::::ssss::::::s
                 s::::::::::::::s
                  s:::::::::::ss
                   sssssssssss""",
            't', """

                          tttt
                       ttt:::t
                       t:::::t
                       t:::::t
                 ttttttt:::::ttttttt
                 t:::::::::::::::::t
                 t:::::::::::::::::t
                 tttttt:::::::tttttt
                       t:::::t
                       t:::::t
                       t:::::t
                       t:::::t    tttttt
                       t::::::tttt:::::t
                       tt::::::::::::::t
                         tt:::::::::::tt
                           ttttttttttt""",
            'u', """





                 uuuuuu    uuuuuu
                 u::::u    u::::u
                 u::::u    u::::u
                 u::::u    u::::u
                 u::::u    u::::u
                 u::::u    u::::u
                 u::::u    u::::u
                 u:::::uuuu:::::u
                 u:::::::::::::::uu
                  u:::::::::::::::u
                   uu::::::::uu:::u
                     uuuuuuuu  uuuu""",
            'v', """





                 vvvvvvv           vvvvvvv
                  v:::::v         v:::::v
                   v:::::v       v:::::v
                    v:::::v     v:::::v
                     v:::::v   v:::::v
                      v:::::v v:::::v
                       v:::::v:::::v
                        v:::::::::v
                         v:::::::v
                          v:::::v
                           v:::v
                            vvv""",
            'w', """





                 wwwwwww           wwwww           wwwwwww
                  w:::::w         w:::::w         w:::::w
                   w:::::w       w:::::::w       w:::::w
                    w:::::w     w:::::::::w     w:::::w
                     w:::::w   w:::::w:::::w   w:::::w
                      w:::::w w:::::w w:::::w w:::::w
                       w:::::w:::::w   w:::::w:::::w
                        w:::::::::w     w:::::::::w
                         w:::::::w       w:::::::w
                          w:::::w         w:::::w
                           w:::w           w:::w
                            www             www""",
            'x', """





                 xxxxxxx      xxxxxxx
                  x:::::x    x:::::x
                   x:::::x  x:::::x
                    x:::::xx:::::x
                     x::::::::::x
                      x::::::::x
                      x::::::::x
                     x::::::::::x
                    x:::::xx:::::x
                   x:::::x  x:::::x
                  x:::::x    x:::::x
                 xxxxxxx      xxxxxxx""",
            'y', """





                 yyyyyyy           yyyyyyy
                  y:::::y         y:::::y
                   y:::::y       y:::::y
                    y:::::y     y:::::y
                     y:::::y   y:::::y
                      y:::::y y:::::y
                       y:::::y:::::y
                        y:::::::::y
                         y:::::::y
                          y:::::y
                         y:::::y
                        y:::::y
                       y:::::y
                      y:::::y
                     y:::::y
                    y:::::y
                   yyyyyyy""",
            'z', """





                 zzzzzzzzzzzzzzzzz
                 z:::::::::::::::z
                 z::::::::::::::z
                 zzzzzzzz::::::z
                       z::::::z
                      z::::::z
                     z::::::z
                    z::::::z
                   z::::::zzzzzzzz
                  z::::::::::::::z
                 z:::::::::::::::z
                 zzzzzzzzzzzzzzzzz""");

    protected static final Map<Character, String> SPECIAL = map(
            ' ', "    ");
}
