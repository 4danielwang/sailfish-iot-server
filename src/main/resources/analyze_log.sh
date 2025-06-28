#!/bin/bash

# TCP日志分析脚本 - 检测粘包拆包问题
# 使用方法: ./analyze_tcp_logs.sh <log_file>

LOG_FILE="$1"

if [ -z "$LOG_FILE" ]; then
    echo "使用方法: $0 <log_file>"
    echo "示例: $0 tcp_server.log"
    exit 1
fi

if [ ! -f "$LOG_FILE" ]; then
    echo "错误: 日志文件 '$LOG_FILE' 不存在"
    exit 1
fi

echo "=== TCP粘包拆包分析报告 ==="
echo "日志文件: $LOG_FILE"
echo "分析时间: $(date)"
echo ""

# 1. 统计连接数量
echo "1. 连接统计:"
echo "总连接数: $(grep -o '0x[0-9a-f]\+' "$LOG_FILE" | sort | uniq | wc -l)"
echo "活跃连接: $(grep -o '0x[0-9a-f]\+' "$LOG_FILE" | sort | uniq | head -10 | tr '\n' ' ')"
echo ""

# 2. 检查报文完整性
echo "2. 报文完整性检查:"
echo "起始标志检查:"
grep "起始标志" "$LOG_FILE" | wc -l | xargs echo "  起始标志数量:"
grep "起始标志.*104" "$LOG_FILE" | wc -l | xargs echo "  正确起始标志(104)数量:"

echo ""
echo "数据长度检查:"
grep "数据长度" "$LOG_FILE" | wc -l | xargs echo "  数据长度字段数量:"
grep "数据长度.*15" "$LOG_FILE" | wc -l | xargs echo "  正确数据长度(15)数量:"

echo ""
echo "完整报文检查:"
# 检查是否有完整的报文解析记录
COMPLETE_PACKETS=$(grep -c "decode message" "$LOG_FILE")
echo "  完整解析的报文数量: $COMPLETE_PACKETS"

# 3. 检测潜在的粘包问题
echo ""
echo "3. 粘包检测:"
echo "检查同一连接中是否有连续的起始标志:"
for conn in $(grep -o '0x[0-9a-f]\+' "$LOG_FILE" | sort | uniq); do
    START_FLAGS=$(grep "$conn" "$LOG_FILE" | grep "起始标志" | wc -l)
    if [ "$START_FLAGS" -gt 1 ]; then
        echo "  连接 $conn: $START_FLAGS 个起始标志 (可能存在粘包)"
    fi
done

# 4. 检测潜在的拆包问题
echo ""
echo "4. 拆包检测:"
echo "检查是否有不完整的报文解析:"
INCOMPLETE=$(grep "起始标志" "$LOG_FILE" | wc -l)
COMPLETE=$(grep "decode message" "$LOG_FILE" | wc -l)
if [ "$INCOMPLETE" -gt "$COMPLETE" ]; then
    echo "  警告: 可能存在拆包问题"
    echo "  起始标志数量: $INCOMPLETE"
    echo "  完整解析数量: $COMPLETE"
    echo "  差异: $((INCOMPLETE - COMPLETE))"
else
    echo "  未发现明显的拆包问题"
fi

# 5. 时间戳分析
echo ""
echo "5. 时间戳分析:"
echo "检查报文接收时间间隔:"
grep "起始标志" "$LOG_FILE" | awk '{print $1}' | sort | uniq -c | head -5 | while read count time; do
    echo "  时间 $time: $count 个起始标志"
done

# 6. 序列号分析
echo ""
echo "6. 序列号分析:"
echo "检查序列号是否连续:"
grep "序列号" "$LOG_FILE" | head -10 | while read line; do
    seq=$(echo "$line" | grep -o '\[[0-9]*, [0-9]*\]')
    echo "  序列号: $seq"
done

# 7. 建议
echo ""
echo "7. 分析建议:"
echo "✓ 如果起始标志数量 > 完整解析数量，可能存在拆包"
echo "✓ 如果同一连接有多个起始标志，可能存在粘包"
echo "✓ 如果序列号不连续，可能存在丢包或乱序"
echo "✓ 如果时间戳过于密集，可能存在网络拥塞"

echo ""
echo "=== 分析完成 ==="
