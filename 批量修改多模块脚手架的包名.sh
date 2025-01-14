#!/bin/bash

# 设置脚本编码为 UTF-8
export LC_ALL=C.UTF-8

# 提示用户输入顶级域名
read -p "请提供一个顶级域名，例如 example.com: " domain

# 根据 '.' 分割域名
subdomain=$(echo "$domain" | cut -d'.' -f1)
tld=$(echo "$domain" | cut -d'.' -f2)

# 检查分割结果
if [[ -z "$subdomain" ]]; then
    echo "无法解析域名的次级域名部分，请检查输入。"
    exit 1
fi

if [[ -z "$tld" ]]; then
    echo "无法解析域名的顶级域名部分，请检查输入。"
    exit 1
fi

# 输出分割结果
echo "一级域名: $subdomain"
echo "顶级域名: $tld"
echo "域名反写结果：$tld.$subdomain"

# 提示用户确认
read -p "是否继续重命名文件夹? 按 Y 确认, 其他键取消: " confirm
if [[ "${confirm,,}" != "y" ]]; then
    echo "操作已取消。"
    exit 0
fi

# 定义根目录（当前文件夹）
root_dir=$(pwd)

# 第一次循环：重命名所有的 uncarbon 文件夹为一级域名
find "$root_dir" -type d -name "uncarbon" | while read -r dir; do
    if [[ -d "$dir" ]]; then
        new_name=$(dirname "$dir")/"$subdomain"
        echo "正在将 $dir 重命名为 $new_name"
        mv "$dir" "$new_name"
    fi
done

# 第二次循环：重命名所有的 cc 文件夹为顶级域名
find "$root_dir" -type d -name "cc" | while read -r dir; do
    if [[ -d "$dir" ]]; then
        new_name=$(dirname "$dir")/"$tld"
        echo "正在将 $dir 重命名为 $new_name"
        mv "$dir" "$new_name"
    fi
done

echo "所有文件夹已重命名完成！"
echo "操作完成。"