#!/bin/bash

# 测试粘包拆包问题的脚本
# 发送100个报文到TCP服务器

if [ $# -ne 2 ]; then
    echo "用法: $0 <服务器地址> <端口>"
    echo "示例: $0 localhost 8080"
    exit 1
fi

SERVER_ADDR=$1
SERVER_PORT=$2

echo "开始测试粘包拆包问题..."
echo "目标服务器: ${SERVER_ADDR}:${SERVER_PORT}"
echo "发送报文数量: 100"
echo ""

# 测试方法1: 快速连续发送 (可能产生粘包)
echo "=== 测试1: 快速连续发送 (可能产生粘包) ==="
for i in {1..100}; do
    printf '\x68\x0F\x00\x01\x00\x01Hello World\x00\x00' | nc "$SERVER_ADDR" "$SERVER_PORT" &
done
wait
echo "快速发送完成，等待2秒..."
sleep 2

echo ""
echo "=== 测试2: 带延迟发送 (减少粘包概率) ==="
for i in {1..100}; do
    printf '\x68\x0F\x00\x01\x00\x01Hello World\x00\x00' | nc "$SERVER_ADDR" "$SERVER_PORT"
    sleep 0.01  # 10ms延迟
done

echo ""
echo "=== 测试3: 发送不同长度的报文 (测试拆包) ==="
for i in {1..50}; do
    # 短报文
    printf '\x68\x05\x00\x01\x00\x01Hi\x00\x00' | nc "$SERVER_ADDR" "$SERVER_PORT"
    sleep 0.01

    # 长报文
    printf '\x68\x15\x00\x01\x00\x01This is a longer message\x00\x00' | nc "$SERVER_ADDR" "$SERVER_PORT"
    sleep 0.01
done

echo ""
echo "=== 测试4: 发送大量数据 (测试缓冲区) ==="
for i in {1..20}; do
    # 构造接近200B的报文
    LONG_MSG=$(printf 'A%.0s' {1..180})  # 180个A字符
    MSG_LEN=$(printf "%02X" $((2 + 1 + 1 + 180)))
    printf "\x68\x${MSG_LEN}\x00\x01\x00\x01${LONG_MSG}\x00\x00" | nc "$SERVER_ADDR" "$SERVER_PORT"
    sleep 0.05
done

echo ""
echo "所有测试完成!"
echo ""
echo "测试说明:"
echo "1. 快速连续发送: 测试服务器是否能正确处理粘包"
echo "2. 带延迟发送: 减少粘包概率，测试正常情况"
echo "3. 不同长度报文: 测试服务器是否能正确解析不同长度的报文"
echo "4. 大量数据: 测试服务器缓冲区处理能力"
echo ""
echo "请检查服务器日志，观察是否有以下问题:"
echo "- 报文丢失"
echo "- 报文重复"
echo "- 报文内容错误"
echo "- 服务器崩溃或异常"
